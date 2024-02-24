package ru.fedbon.commentservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fedbon.commentservice.dto.BookResponseDto;
import ru.fedbon.commentservice.dto.CommentByBookDto;
import ru.fedbon.commentservice.exception.NotFoundException;
import ru.fedbon.commentservice.repository.CommentRepository;
import ru.fedbon.commentservice.dto.CommentDto;
import ru.fedbon.commentservice.mapper.CommentMapper;


import java.util.Comparator;

import static ru.fedbon.commentservice.constants.AppConstants.CIRCUIT_BREAKER_BOOK_SERVICE;
import static ru.fedbon.commentservice.constants.ErrorMessage.AUTHOR_INFORMATION_NOT_AVAILABLE_MESSAGE;
import static ru.fedbon.commentservice.constants.ErrorMessage.BOOK_INFORMATION_NOT_AVAILABLE_MESSAGE;
import static ru.fedbon.commentservice.constants.ErrorMessage.COMMENTS_NOT_FOUND_BY_BOOK_ID;
import static ru.fedbon.commentservice.constants.ErrorMessage.COMMENTS_NOT_FOUND_BY_USER_ID;
import static ru.fedbon.commentservice.constants.Message.COUNTED_COMMENTS_BY_USER_ID_MESSAGE;
import static ru.fedbon.commentservice.constants.Message.RECEIVED_BOOK_MESSAGE;
import static ru.fedbon.commentservice.constants.Message.SUCCESSFULLY_RETRIEVED_COMMENTS_BY_BOOK_ID_MESSAGE;
import static ru.fedbon.commentservice.constants.Message.SUCCESSFULLY_RETRIEVED_COMMENTS_BY_USER_ID_MESSAGE;
import static ru.fedbon.commentservice.constants.PathConstants.API_V1_BOOKS;
import static ru.fedbon.commentservice.constants.PathConstants.API_V1_COMMENTS;
import static ru.fedbon.commentservice.constants.PathConstants.BOOK_ID;
import static ru.fedbon.commentservice.constants.PathConstants.COMMENTS_BY_BOOK_ID;
import static ru.fedbon.commentservice.constants.PathConstants.COMMENTS_BY_USER_ID;
import static ru.fedbon.commentservice.constants.PathConstants.COUNT_COMMENTS_BY_USER_ID;
import static ru.fedbon.commentservice.constants.PathConstants.CREATED_AT;
import static ru.fedbon.commentservice.constants.PathConstants.ID;
import static ru.fedbon.commentservice.constants.WebClientConstants.BOOK_SERVICE;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = API_V1_COMMENTS)
public class CommentRestController {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final ReactiveCircuitBreakerFactory cbFactory;

    private final WebClient.Builder webClientBuilder;

    @GetMapping(value = COUNT_COMMENTS_BY_USER_ID)
    public Mono<Long> handleCountCommentsByUserId(@PathVariable(value = ID) String userId) {
        return commentRepository.countByUserId(userId)
                .doOnSuccess(count -> log.info(COUNTED_COMMENTS_BY_USER_ID_MESSAGE, userId, count))
                .onErrorResume(ex ->
                        Mono.error(new NotFoundException(COMMENTS_NOT_FOUND_BY_USER_ID + userId)));
    }

    @GetMapping(value = COMMENTS_BY_BOOK_ID)
    public Flux<CommentDto> handleGetAllByBookId(@PathVariable(value = ID) String bookId) {
        return commentRepository.findAllByBookId(Sort.by(Sort.Direction.DESC, CREATED_AT), bookId)
                .map(commentMapper::mapToCommentDto)
                .doOnComplete(() -> log.info(SUCCESSFULLY_RETRIEVED_COMMENTS_BY_BOOK_ID_MESSAGE, bookId))
                .onErrorResume(ex ->
                        Flux.error(new NotFoundException(COMMENTS_NOT_FOUND_BY_BOOK_ID + bookId)));
    }

    @GetMapping(value = COMMENTS_BY_USER_ID)
    public Flux<CommentByBookDto> handleGetAllByUserId(@PathVariable(value = ID) String userId) {
        var userComments = commentRepository.findAllByUserId(
                Sort.by(Sort.Direction.DESC, CREATED_AT), userId);
        return userComments
                .flatMap(comment -> fetchBookInfo(comment.getBookId())
                        .map(bookInfo -> {
                            var commentDto = commentMapper.mapToCommentByBookDto(comment);
                            commentDto.setBookName(bookInfo.getTitle());
                            commentDto.setAuthorName(bookInfo.getAuthorName());
                            return commentDto;
                        }))
                .sort(Comparator.comparing(CommentByBookDto::getTimeAgo).reversed())
                .doOnComplete(() -> log.info(SUCCESSFULLY_RETRIEVED_COMMENTS_BY_USER_ID_MESSAGE, userId))
                .onErrorResume(ex ->
                        Flux.error(new NotFoundException(COMMENTS_NOT_FOUND_BY_USER_ID + userId)));
    }

    private Mono<BookResponseDto> fetchBookInfo(String bookId) {
        return webClientBuilder.build()
                .get()
                .uri(BOOK_SERVICE + API_V1_BOOKS + BOOK_ID, bookId)
                .retrieve()
                .bodyToMono(BookResponseDto.class)
                .transform(it -> cbFactory.create(CIRCUIT_BREAKER_BOOK_SERVICE)
                        .run(it, throwable -> Mono.just(new BookResponseDto(null,
                                BOOK_INFORMATION_NOT_AVAILABLE_MESSAGE, AUTHOR_INFORMATION_NOT_AVAILABLE_MESSAGE))))
                .doOnNext(bookDto -> log.info(RECEIVED_BOOK_MESSAGE, bookDto));
    }
}
