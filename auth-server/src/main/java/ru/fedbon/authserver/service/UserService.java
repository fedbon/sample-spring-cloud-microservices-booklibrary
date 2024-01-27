package ru.fedbon.authserver.service;

import reactor.core.publisher.Mono;
import ru.fedbon.authserver.model.User;

public interface UserService {

    Mono<User> registerUser(User user);

    Mono<User> getUserById(String id);

    Mono<User> getUserByUsername(String username);
}
