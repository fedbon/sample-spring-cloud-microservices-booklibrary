package ru.fedbon.authserver.service;

import reactor.core.publisher.Mono;
import ru.fedbon.authserver.dto.UserValidationResponse;
import ru.fedbon.authserver.vo.TokenDetailsVo;

public interface AuthService {

    Mono<TokenDetailsVo> authenticate(String username, String password);

    Mono<UserValidationResponse> validateToken(String token);
}
