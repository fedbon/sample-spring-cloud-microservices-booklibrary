package ru.fedbon.voteservice.repository;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import ru.fedbon.voteservice.model.VoteComment;

public interface VoteCommentRepository extends ReactiveMongoRepository<VoteComment, String> {

    Mono<VoteComment> findByUserId(String userId);
}
