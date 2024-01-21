package ru.fedbon.bookservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.fedbon.bookservice.model.Author;


public interface AuthorRepository extends ReactiveMongoRepository<Author, String> {

}
