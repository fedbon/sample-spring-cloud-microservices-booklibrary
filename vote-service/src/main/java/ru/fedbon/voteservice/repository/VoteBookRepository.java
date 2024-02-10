package ru.fedbon.voteservice.repository;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import ru.fedbon.voteservice.model.VoteBook;

public interface VoteBookRepository extends ReactiveMongoRepository<VoteBook, String> {

    Mono<VoteBook> findByUserId(String userId);
}
