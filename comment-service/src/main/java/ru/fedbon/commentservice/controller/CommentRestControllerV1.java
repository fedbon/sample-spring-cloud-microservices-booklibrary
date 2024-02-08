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


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/comments")
public class CommentRestControllerV1 {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final ReactiveCircuitBreakerFactory cbFactory;

    private final WebClient.Builder webClientBuilder;

    @GetMapping(value = "/user/{id}/count")
    public Mono<Long> handleCountCommentsByUserId(@PathVariable(value = "id") String userId) {
        return commentRepository.countByUserId(userId)
                .doOnSuccess(count -> log.info("Count of comments for userId {}: {}", userId, count))
                .onErrorResume(ex ->
                        Mono.error(new NotFoundException("Failed to count comments by userId")));
    }

    @GetMapping(value = "/book/{id}")
    public Flux<CommentDto> handleGetAllByBookId(@PathVariable(value = "id") String bookId) {
        return commentRepository.findAllByBookId(Sort.by(Sort.Direction.DESC, "createdAt"), bookId)
                .map(commentMapper::mapToCommentDto)
                .doOnComplete(() -> log.info("All reviews retrieved successfully for bookId: {}", bookId))
                .onErrorResume(ex ->
                        Flux.error(new NotFoundException("Failed to retrieve reviews by bookId")));
    }

    @GetMapping(value = "/user/{id}")
    public Flux<CommentByBookDto> handleGetAllByUserId(@PathVariable(value = "id") String userId) {
        var userComments = commentRepository.findAllByUserId(Sort.by(Sort.Direction.DESC, "createdAt"),
                userId);

        return userComments
                .flatMap(comment -> fetchBookInfo(comment.getBookId())
                        .map(bookInfo -> {
                            var commentDto = commentMapper.mapToCommentByBookDto(comment);
                            commentDto.setBookName(bookInfo.getTitle());
                            commentDto.setAuthorName(bookInfo.getAuthorName());
                            return commentDto;
                        }))
                .sort(Comparator.comparing(CommentByBookDto::getTimeAgo).reversed())
                .doOnComplete(() -> log.info("All reviews retrieved successfully for userId: {}", userId))
                .onErrorResume(ex ->
                        Flux.error(new NotFoundException("Failed to retrieve reviews by userId")));
    }

    private Mono<BookResponseDto> fetchBookInfo(String bookId) {
        return webClientBuilder.build()
                .get()
                .uri("http://book-service/api/v1/books/{id}", bookId)
                .retrieve()
                .bodyToMono(BookResponseDto.class)
                .transform(it -> cbFactory.create("book-service")
                        .run(it, throwable -> Mono.just(new BookResponseDto(null,
                                "Book information not available", "Author information not available"))))
                .doOnNext(bookDto -> log.info("Received book information: {}", bookDto));
    }
}
