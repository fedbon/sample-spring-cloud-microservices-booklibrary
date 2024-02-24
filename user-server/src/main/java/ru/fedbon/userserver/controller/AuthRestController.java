package ru.fedbon.userserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.fedbon.userserver.dto.security.AuthRequestDto;
import ru.fedbon.userserver.dto.security.AuthResponseDto;
import ru.fedbon.userserver.dto.security.RegistrationRequest;
import ru.fedbon.userserver.dto.security.RegistrationResponse;
import ru.fedbon.userserver.dto.security.UserValidationResponse;
import ru.fedbon.userserver.mapper.UserMapper;
import ru.fedbon.userserver.service.AuthService;
import reactor.core.publisher.Mono;

import static ru.fedbon.userserver.constants.PathConstants.API_V1_AUTH;
import static ru.fedbon.userserver.constants.PathConstants.SIGN_IN;
import static ru.fedbon.userserver.constants.PathConstants.SIGN_UP;
import static ru.fedbon.userserver.constants.PathConstants.TOKEN_PARAM;
import static ru.fedbon.userserver.constants.PathConstants.VALIDATE;


@RestController
@RequiredArgsConstructor
@RequestMapping(API_V1_AUTH)
public class AuthRestController {

    private final AuthService authService;

    private final UserMapper userMapper;

    @PostMapping(SIGN_UP)
    public Mono<RegistrationResponse> handleSignUp(@RequestBody RegistrationRequest registrationRequest) {
        return authService.registerUser(userMapper.mapRegistrationRequestToUser(registrationRequest))
                .map(userMapper::mapUserToRegistrationResponse);
    }

    @PostMapping(SIGN_IN)
    public Mono<AuthResponseDto> handleSignIn(@RequestBody AuthRequestDto dto) {
        return authService.authenticate(dto.getUsername(), dto.getPassword())
                .flatMap(tokenDetails -> Mono.just(
                        AuthResponseDto.builder()
                                .userId(tokenDetails.getUserId())
                                .token(tokenDetails.getToken())
                                .issuedAt(tokenDetails.getIssuedAt())
                                .expiresAt(tokenDetails.getExpiresAt())
                                .build()
                ));
    }

    @PostMapping(VALIDATE)
    public Mono<UserValidationResponse> handleValidateToken(@RequestParam(TOKEN_PARAM) String token) {
        return authService.validateToken(token);
    }
}
