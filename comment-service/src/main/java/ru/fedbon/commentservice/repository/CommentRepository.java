package ru.fedbon.commentservice.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fedbon.commentservice.model.Comment;


public interface CommentRepository extends ReactiveMongoRepository<Comment, String> {

    Flux<Comment> findAllByBookId(Sort sort, String bookId);

    Flux<Comment> findAllByUserId(Sort sort, String userId);

    Mono<Long> countByUserId(String userId);
}
