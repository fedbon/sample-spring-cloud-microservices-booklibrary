package ru.fedbon.bookservice.repository;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import ru.fedbon.bookservice.model.Book;



public interface BookRepository extends ReactiveMongoRepository<Book, String> {

    Flux<Book> findAllByGenreId(String genreId);

    Flux<Book> findAllByAuthorId(String authorId);
}
