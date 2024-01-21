package ru.fedbon.securityservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.fedbon.securityservice.dto.AuthRequestDto;
import ru.fedbon.securityservice.dto.AuthResponseDto;
import ru.fedbon.securityservice.dto.UserDto;
import ru.fedbon.securityservice.dto.UserValidationResponse;
import ru.fedbon.securityservice.model.User;
import ru.fedbon.securityservice.mapper.UserMapper;
import ru.fedbon.securityservice.dto.security.CustomPrincipal;
import ru.fedbon.securityservice.service.AuthService;
import ru.fedbon.securityservice.service.UserService;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthRestControllerV1 {

    private final AuthService authService;

    private final UserService userService;

    private final UserMapper userMapper;


    @PostMapping("/register")
    public Mono<UserDto> register(@RequestBody UserDto dto) {
        User entity = userMapper.map(dto);
        return userService.registerUser(entity)
                .map(userMapper::map);
    }

    @PostMapping("/login")
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

    @PostMapping("/validate-token")
    public Mono<UserValidationResponse> validateToken(@RequestParam("token") String token) {
        return authService.validateToken(token);
    }

    @GetMapping("/info")
    public Mono<UserDto> getCurrentUser(Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();

        return userService.getUserById(customPrincipal.getId())
                .map(userMapper::map);
    }
}
