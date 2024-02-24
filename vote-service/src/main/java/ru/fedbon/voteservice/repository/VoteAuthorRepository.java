package ru.fedbon.voteservice.repository;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import ru.fedbon.voteservice.model.VoteAuthor;


public interface VoteAuthorRepository extends ReactiveMongoRepository<VoteAuthor, String> {

    Mono<VoteAuthor> findByUserId(String userId);
}
