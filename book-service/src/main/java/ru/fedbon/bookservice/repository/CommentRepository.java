package ru.fedbon.bookservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import ru.fedbon.bookservice.model.Book;
import ru.fedbon.bookservice.model.Comment;




public interface CommentRepository extends ReactiveMongoRepository<Comment, String> {

    Flux<Comment> findAllByBook(Book book);
}
