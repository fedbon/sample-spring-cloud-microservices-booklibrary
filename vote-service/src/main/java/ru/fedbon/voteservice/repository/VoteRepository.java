package ru.fedbon.voteservice.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import ru.fedbon.voteservice.model.Vote;


public interface VoteRepository extends ReactiveMongoRepository<Vote, String> {

    Flux<Vote> findAllByBookId(Sort sort, String bookId);

    Flux<Vote> findAllByUserId(Sort sort, String userId);
}
