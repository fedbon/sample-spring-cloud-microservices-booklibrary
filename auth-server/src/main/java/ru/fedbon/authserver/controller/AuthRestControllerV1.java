package ru.fedbon.authserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.fedbon.authserver.dto.security.AuthRequestDto;
import ru.fedbon.authserver.dto.security.AuthResponseDto;
import ru.fedbon.authserver.dto.UserDto;
import ru.fedbon.authserver.dto.UserValidationResponse;
import ru.fedbon.authserver.mapper.UserMapper;
import ru.fedbon.authserver.service.AuthService;
import reactor.core.publisher.Mono;
import ru.fedbon.authserver.service.UserService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthRestControllerV1 {

    private final AuthService authService;

    private final UserService userService;

    private final UserMapper userMapper;

    @PostMapping("/signup")
    public Mono<UserDto> register(@RequestBody UserDto dto) {
        return userService.registerUser(userMapper.map(dto))
                .map(userMapper::map);
    }

    @PostMapping("/signin")
    public Mono<AuthResponseDto> login(@RequestBody AuthRequestDto dto) {
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
    public Mono<UserValidationResponse> validateToken(@RequestParam("token") String token) {
        return authService.validateToken(token);
    }
}
