package ru.fedbon.userserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.fedbon.userserver.dto.AccountDto;
import ru.fedbon.userserver.dto.UserDto;
import ru.fedbon.userserver.dto.security.CustomPrincipal;
import ru.fedbon.userserver.exception.InvalidCredentialsException;
import ru.fedbon.userserver.exception.NotFoundException;
import ru.fedbon.userserver.mapper.UserMapper;
import ru.fedbon.userserver.repository.UserRepository;

import java.util.Objects;

import static ru.fedbon.userserver.constants.AppConstants.BAD_COUNT_AUTHOR_SERVICE_RESPONSE;
import static ru.fedbon.userserver.constants.AppConstants.BAD_COUNT_BOOK_SERVICE_RESPONSE;
import static ru.fedbon.userserver.constants.AppConstants.BAD_COUNT_COMMENT_SERVICE_RESPONSE;
import static ru.fedbon.userserver.constants.AppConstants.CIRCUIT_BREAKER_AUTHOR_SERVICE;
import static ru.fedbon.userserver.constants.AppConstants.CIRCUIT_BREAKER_BOOK_SERVICE;
import static ru.fedbon.userserver.constants.AppConstants.CIRCUIT_BREAKER_COMMENT_SERVICE;
import static ru.fedbon.userserver.constants.ErrorMessage.AUTHORS_NOT_FOUND_BY_USER_ID;
import static ru.fedbon.userserver.constants.ErrorMessage.BOOKS_NOT_FOUND_BY_USER_ID;
import static ru.fedbon.userserver.constants.ErrorMessage.COMMENTS_NOT_FOUND_BY_USER_ID;
import static ru.fedbon.userserver.constants.ErrorMessage.FAILED_TO_FETCH_AUTHORS_BY_USER_ID;
import static ru.fedbon.userserver.constants.ErrorMessage.FAILED_TO_FETCH_BOOKS_BY_USER_ID;
import static ru.fedbon.userserver.constants.ErrorMessage.FAILED_TO_FETCH_COMMENTS_BY_USER_ID;
import static ru.fedbon.userserver.constants.ErrorMessage.MISSED_AUTHORIZATION_HEADER;
import static ru.fedbon.userserver.constants.ErrorMessage.USER_NOT_FOUND;
import static ru.fedbon.userserver.constants.Message.COUNTED_AUTHORS_BY_USER_ID_MESSAGE;
import static ru.fedbon.userserver.constants.Message.COUNTED_BOOKS_BY_USER_ID_MESSAGE;
import static ru.fedbon.userserver.constants.Message.COUNTED_COMMENTS_BY_USER_ID_MESSAGE;
import static ru.fedbon.userserver.constants.PathConstants.API_V1_AUTHORS;
import static ru.fedbon.userserver.constants.PathConstants.API_V1_BOOKS;
import static ru.fedbon.userserver.constants.PathConstants.API_V1_COMMENTS;
import static ru.fedbon.userserver.constants.PathConstants.API_V1_USER;
import static ru.fedbon.userserver.constants.PathConstants.COUNT_COMMENTS_BY_USER_ID;
import static ru.fedbon.userserver.constants.PathConstants.COUNT_VOTES_BY_AUTHOR_ID;
import static ru.fedbon.userserver.constants.PathConstants.COUNT_VOTES_BY_BOOK_ID;
import static ru.fedbon.userserver.constants.PathConstants.MY_ACCOUNT;
import static ru.fedbon.userserver.constants.PathConstants.USER_ID;
import static ru.fedbon.userserver.constants.WebClientConstants.AUTHOR_SERVICE;
import static ru.fedbon.userserver.constants.WebClientConstants.BOOK_SERVICE;
import static ru.fedbon.userserver.constants.WebClientConstants.COMMENT_SERVICE;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(API_V1_USER)
public class UserRestController {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final ReactiveCircuitBreakerFactory cbFactory;

    private final WebClient.Builder webClientBuilder;

    @GetMapping(MY_ACCOUNT)
    public Mono<AccountDto> handleGetAccountInfo(Authentication authentication) {
        if (authentication != null) {
            var customPrincipal = (CustomPrincipal) authentication.getPrincipal();
            var userMono = userRepository.findById(customPrincipal.getId());
            var booksCountMono = fetchBooksCount("1"); //stub should be changed to customPrincipal.getId()
            var authorsCountMono = fetchAuthorsCount("1"); //stub should be changed to customPrincipal.getId()

            return Mono.zip(userMono, booksCountMono, authorsCountMono).map(tuple -> {
                var user = tuple.getT1();
                var booksCount = tuple.getT2();
                var authorsCount = tuple.getT3();
                var accountDto = userMapper.mapUserToAccountDto(user);
                accountDto.setBooksCount(booksCount.intValue());
                accountDto.setAuthorsCount(authorsCount.intValue());
                return accountDto;
            }).switchIfEmpty(Mono.error(new NotFoundException(USER_NOT_FOUND + customPrincipal.getId())));
        } else {
            return Mono.error(new InvalidCredentialsException(MISSED_AUTHORIZATION_HEADER));
        }
    }

    @GetMapping(USER_ID)
    public Mono<UserDto> handleGetUserById(@PathVariable String id) {
        var userMono = userRepository.findById(id);

        var commentsCountMono = fetchCommentsCount("1"); //stub should be changed to id
        var booksCountMono = fetchBooksCount("1"); //stub should be changed to id

        return Mono.zip(userMono, commentsCountMono, booksCountMono).map(tuple -> {
            var user = tuple.getT1();
            var commentsCount = tuple.getT2();
            var booksCount = tuple.getT3();

            var userDto = userMapper.mapUserToUserDto(user);
            userDto.setCommentsCount(commentsCount.intValue());
            userDto.setBooksCount(booksCount.intValue());

            return userDto;
        }).switchIfEmpty(Mono.error(new NotFoundException(USER_NOT_FOUND + id)));
    }

    public Mono<Long> fetchAuthorsCount(String userId) {
        return webClientBuilder.build()
                .get()
                .uri(AUTHOR_SERVICE + API_V1_AUTHORS + COUNT_VOTES_BY_AUTHOR_ID, userId)
                .retrieve()
                .bodyToMono(Long.class)
                .transform(it -> cbFactory.create(CIRCUIT_BREAKER_AUTHOR_SERVICE)
                        .run(it, throwable -> Mono.just(BAD_COUNT_AUTHOR_SERVICE_RESPONSE)))
                .doOnSuccess(count -> {
            if (Objects.equals(count, BAD_COUNT_AUTHOR_SERVICE_RESPONSE)) {
                log.warn(FAILED_TO_FETCH_AUTHORS_BY_USER_ID, userId);
            } else {
                log.info(COUNTED_AUTHORS_BY_USER_ID_MESSAGE, userId, count);
            }
        }).onErrorResume(ex -> Mono.error(new NotFoundException(AUTHORS_NOT_FOUND_BY_USER_ID + userId)));
    }

    public Mono<Long> fetchBooksCount(String userId) {
        return webClientBuilder.build()
                .get()
                .uri(BOOK_SERVICE + API_V1_BOOKS + COUNT_VOTES_BY_BOOK_ID, userId)
                .retrieve()
                .bodyToMono(Long.class)
                .transform(it -> cbFactory.create(CIRCUIT_BREAKER_BOOK_SERVICE)
                        .run(it, throwable -> Mono.just(BAD_COUNT_BOOK_SERVICE_RESPONSE)))
                .doOnSuccess(count -> {
            if (Objects.equals(count, BAD_COUNT_BOOK_SERVICE_RESPONSE)) {
                log.warn(FAILED_TO_FETCH_BOOKS_BY_USER_ID, userId);
            } else {
                log.info(COUNTED_BOOKS_BY_USER_ID_MESSAGE, userId, count);
            }
        }).onErrorResume(ex -> Mono.error(new NotFoundException(BOOKS_NOT_FOUND_BY_USER_ID + userId)));
    }

    private Mono<Long> fetchCommentsCount(String userId) {
        return webClientBuilder.build()
                .get()
                .uri(COMMENT_SERVICE + API_V1_COMMENTS + COUNT_COMMENTS_BY_USER_ID, userId)
                .retrieve()
                .bodyToMono(Long.class)
                .transform(it -> cbFactory.create(CIRCUIT_BREAKER_COMMENT_SERVICE)
                        .run(it, throwable -> Mono.just(BAD_COUNT_COMMENT_SERVICE_RESPONSE)))
                .doOnSuccess(count -> {
            if (Objects.equals(count, BAD_COUNT_COMMENT_SERVICE_RESPONSE)) {
                log.warn(FAILED_TO_FETCH_COMMENTS_BY_USER_ID, userId);
            } else {
                log.info(COUNTED_COMMENTS_BY_USER_ID_MESSAGE, userId, count);
            }
        }).onErrorResume(ex -> Mono.error(new NotFoundException(COMMENTS_NOT_FOUND_BY_USER_ID + userId)));
    }
}
