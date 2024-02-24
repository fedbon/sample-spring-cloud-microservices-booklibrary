package ru.fedbon.authorservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.fedbon.authorservice.model.Author;


public interface AuthorRepository extends ReactiveMongoRepository<Author, String> {

}
