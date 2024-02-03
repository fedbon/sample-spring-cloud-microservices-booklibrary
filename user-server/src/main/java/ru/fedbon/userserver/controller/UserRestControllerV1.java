package ru.fedbon.userserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.fedbon.userserver.dto.AccountDto;
import ru.fedbon.userserver.dto.UserDto;
import ru.fedbon.userserver.dto.security.CustomPrincipal;
import ru.fedbon.userserver.exception.InvalidCredentialsException;
import ru.fedbon.userserver.exception.NotFoundException;
import ru.fedbon.userserver.mapper.UserMapper;
import ru.fedbon.userserver.repository.UserRepository;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserRestControllerV1 {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @GetMapping("/my")
    public Mono<AccountDto> handleGetAccountInfo(Authentication authentication) {
        if (authentication != null) {
            var customPrincipal = (CustomPrincipal) authentication.getPrincipal();
            return userRepository.findById(customPrincipal.getId())
                    .map(userMapper::mapUserToAccountDto);
        } else {
            return Mono.error(new InvalidCredentialsException("Authorization header is empty"));
        }
    }

    @GetMapping("/{userId}")
    public Mono<UserDto> handleGetUserById(@PathVariable String userId) {
        return userRepository.findById(userId)
                .map(userMapper::mapUserToUserDto)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found with id: " + userId)));
    }

    @GetMapping("/{username}")
    public Mono<UserDto> handleGetUserByUsername(@PathVariable String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::mapUserToUserDto)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found with username: " + username)));
    }
}
