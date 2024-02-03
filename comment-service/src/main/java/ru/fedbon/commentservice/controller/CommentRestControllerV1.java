package ru.fedbon.commentservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.fedbon.commentservice.exception.NotFoundException;
import ru.fedbon.commentservice.model.Comment;
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

    @GetMapping(value = "/book/{id}")
    public Flux<CommentDto> handleGetAllByBookId(@PathVariable(value = "id") String bookId) {
        try {
            return commentRepository.findAllByBookId(Sort.by(Sort.Direction.DESC, "createdAt"), bookId)
                    .map(commentMapper::map)
                    .doOnComplete(() -> log.info("All reviews retrieved successfully for bookTitle: {}", bookId));
        } catch (Exception e) {
            log.error("Error occurred while retrieving reviews for bookTitle: {}", e.getMessage(), e);
            throw new NotFoundException("Failed to retrieve reviews by bookTitle");
        }
    }

    @GetMapping(value = "/user/{id}")
    public Flux<CommentDto> handleGetAllByUserId(@PathVariable(value = "id") String userId) {
        try {
            Flux<Comment> userReviews = commentRepository.findAllByUserId(Sort.by(Sort.Direction.DESC,
                    "createdAt"), userId);

            return userReviews
                    .collectMultimap(Comment::getBookId)
                    .flatMapMany(reviewsByBookId -> Flux.fromIterable(reviewsByBookId.entrySet())
                            .flatMap(entry -> Flux.fromIterable(entry.getValue())
                                    .sort(Comparator.comparing(Comment::getCreatedAt).reversed())
                                    .next()
                                    .map(commentMapper::map)
                            )
                    )
                    .doOnComplete(() -> log.info("All reviews retrieved successfully for username: {}", userId));
        } catch (Exception e) {
            log.error("Error occurred while retrieving reviews by username: {}", e.getMessage(), e);
            throw new NotFoundException("Failed to retrieve reviews by username");
        }
    }
}
