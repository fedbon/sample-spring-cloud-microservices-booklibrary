package ru.fedbon.authserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.fedbon.authserver.dto.UserDto;
import ru.fedbon.authserver.dto.security.CustomPrincipal;
import ru.fedbon.authserver.exception.InvalidCredentialsException;
import ru.fedbon.authserver.mapper.UserMapper;
import ru.fedbon.authserver.service.UserService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserRestControllerV1 {

    private final UserService userService;

    private final UserMapper userMapper;

    @GetMapping("/get-user")
    public Mono<UserDto> getCurrentUser(Authentication authentication) {
        if (authentication != null) {
            var customPrincipal = (CustomPrincipal) authentication.getPrincipal();
            return userService.getUserById(customPrincipal.getId())
                    .map(userMapper::map);
        } else {
            return Mono.error(new InvalidCredentialsException("Authorization header is empty"));
        }
    }
}
