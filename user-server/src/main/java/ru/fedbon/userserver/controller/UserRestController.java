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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserRestController {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final ReactiveCircuitBreakerFactory cbFactory;

    private final WebClient.Builder webClientBuilder;

    @GetMapping("/my")
    public Mono<AccountDto> handleGetAccountInfo(Authentication authentication) {
        if (authentication != null) {
            var customPrincipal = (CustomPrincipal) authentication.getPrincipal();
            var userMono = userRepository.findById(customPrincipal.getId());
            var booksCountMono = fetchBooksCount("1");
            var authorsCountMono = fetchAuthorsCount("1");

            return Mono.zip(userMono, booksCountMono, authorsCountMono)
                    .map(tuple -> {
                        var user = tuple.getT1();
                        var booksCount = tuple.getT2();
                        var authorsCount = tuple.getT3();
                        var accountDto = userMapper.mapUserToAccountDto(user);
                        accountDto.setBooksCount(booksCount.intValue());
                        accountDto.setAuthorsCount(authorsCount.intValue());
                        return accountDto;
                    })
                    .switchIfEmpty(Mono.error(new NotFoundException("User not found")));
        } else {
            return Mono.error(new InvalidCredentialsException("Authorization header is empty"));
        }
    }

    @GetMapping("/{id}")
    public Mono<UserDto> handleGetUserById(@PathVariable String id) {
        var userMono = userRepository.findById(id);

        var commentsCountMono = fetchCommentsCount("1");
        var booksCountMono = fetchBooksCount("1");

        return Mono.zip(userMono, commentsCountMono, booksCountMono)
                .map(tuple -> {
                    var user = tuple.getT1();
                    var commentsCount = tuple.getT2();
                    var booksCount = tuple.getT3();

                    var userDto = userMapper.mapUserToUserDto(user);
                    userDto.setCommentsCount(commentsCount.intValue());
                    userDto.setBooksCount(booksCount.intValue());

                    return userDto;
                })
                .switchIfEmpty(Mono.error(new NotFoundException("User not found with id: " + id)));
    }

    public Mono<Long> fetchAuthorsCount(String userId) {
        return webClientBuilder.build()
                .get()
                .uri("http://author-service/api/v1/authors/user/{id}/count", userId)
                .retrieve()
                .bodyToMono(Long.class)
                .transform(it -> cbFactory.create("author-service")
                        .run(it, throwable -> Mono.just(-1L)))
                .doOnSuccess(count -> {
                    if (count == -1) {
                        log.warn("Failed to fetch comments count for userId {}", userId);
                    } else {
                        log.info("Received comments count for userId {}: {}", userId, count);
                    }
                })
                .onErrorResume(ex ->
                        Mono.error(new NotFoundException("Failed to fetch authors count related to user ID")));
    }

    public Mono<Long> fetchBooksCount(String userId) {
        return webClientBuilder.build()
                .get()
                .uri("http://book-service/api/v1/books/user/{id}/count", userId)
                .retrieve()
                .bodyToMono(Long.class)
                .transform(it -> cbFactory.create("book-service")
                        .run(it, throwable -> Mono.just(-1L)))
                .doOnSuccess(count -> {
                    if (count == -1) {
                        log.warn("Failed to fetch comments count for userId {}", userId);
                    } else {
                        log.info("Received comments count for userId {}: {}", userId, count);
                    }
                })
                .onErrorResume(ex ->
                        Mono.error(new NotFoundException("Failed to fetch books count by user ID")));
    }

    private Mono<Long> fetchCommentsCount(String userId) {
        return webClientBuilder.build()
                .get()
                .uri("http://comment-service/api/v1/comments/user/{id}/count", userId)
                .retrieve()
                .bodyToMono(Long.class)
                .transform(it -> cbFactory.create("comment-service")
                        .run(it, throwable -> Mono.just(-1L)))
                .doOnSuccess(count -> {
                    if (count == -1) {
                        log.warn("Failed to fetch comments count for userId {}", userId);
                    } else {
                        log.info("Received comments count for userId {}: {}", userId, count);
                    }
                })
                .onErrorResume(ex ->
                        Mono.error(new NotFoundException("Failed to fetch comments count by userId")));
    }
}
