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


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthRestControllerV1 {

    private final AuthService authService;

    private final UserMapper userMapper;

    @PostMapping("/signup")
    public Mono<RegistrationResponse> handleSignUp(@RequestBody RegistrationRequest registrationRequest) {
        return authService.registerUser(userMapper.mapRegistrationRequestToUser(registrationRequest))
                .map(userMapper::mapUserToRegistrationResponse);
    }

    @PostMapping("/signin")
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

    @PostMapping("/validate")
    public Mono<UserValidationResponse> handleValidateToken(@RequestParam("token") String token) {
        return authService.validateToken(token);
    }
}
