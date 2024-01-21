package ru.fedbon.bookservice.repository;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.fedbon.bookservice.model.Genre;


public interface GenreRepository extends ReactiveMongoRepository<Genre, String> {

}
