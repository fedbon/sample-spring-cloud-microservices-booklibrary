package ru.fedbon.userserver.service;

import reactor.core.publisher.Mono;
import ru.fedbon.userserver.dto.security.UserValidationResponse;
import ru.fedbon.userserver.model.User;
import ru.fedbon.userserver.vo.TokenDetailsVo;

public interface AuthService {

    Mono<TokenDetailsVo> authenticate(String username, String password);

    Mono<User> registerUser(User user);

    Mono<UserValidationResponse> validateToken(String token);
}
