package ru.fedbon.userserver.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.fedbon.userserver.model.User;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findByUsername(String username);
}
