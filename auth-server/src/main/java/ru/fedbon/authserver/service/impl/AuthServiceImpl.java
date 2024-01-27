package ru.fedbon.authserver.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.fedbon.authserver.dto.UserValidationResponse;
import ru.fedbon.authserver.model.User;
import ru.fedbon.authserver.exception.InvalidCredentialsException;
import ru.fedbon.authserver.vo.TokenDetailsVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.fedbon.authserver.service.AuthService;
import ru.fedbon.authserver.service.UserService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Integer expirationInSeconds;

    @Value("${jwt.issuer}")
    private String issuer;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public Mono<TokenDetailsVo> authenticate(String username, String password) {
        return userService.getUserByUsername(username)
                .flatMap(user -> {
                    if (!user.isEnabled()) {
                        return Mono.error(new InvalidCredentialsException("Account is disabled"));
                    }

                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        return Mono.error(new InvalidCredentialsException("Invalid password"));
                    }

                    return Mono.just(generateToken(user).toBuilder()
                            .userId(user.getId())
                            .build());
                })
                .switchIfEmpty(Mono.error(new InvalidCredentialsException("Invalid username")));
    }

    public Mono<UserValidationResponse> validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String subject = claims.getSubject();
            return userService.getUserById(subject)
                    .map(user -> UserValidationResponse.builder()
                            .id(user.getId())
                            .role(user.getRole())
                            .build());
        } catch (ExpiredJwtException e) {
            log.warn("Expired token: {}", e.getMessage());
            return Mono.error(e);
        } catch (JwtException e) {
            log.error("Invalid token: {}", e.getMessage());
            return Mono.error(new InvalidCredentialsException("Invalid token"));
        }
    }

    private TokenDetailsVo generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
            claims.put("role", user.getRole());
            claims.put("username", user.getUsername());
        return generateToken(claims, user.getId());
    }

    private TokenDetailsVo generateToken(Map<String, Object> claims, String subject) {
        long expirationTimeInMillis = expirationInSeconds * 20L; //token expires in 30 minutes
        Date expirationDate = new Date(new Date().getTime() + expirationTimeInMillis);

        return generateToken(expirationDate, claims, subject);
    }

    private TokenDetailsVo generateToken(Date expirationDate, Map<String, Object> claims, String subject) {
        Date createdDate = new Date();
        String token = Jwts.builder()
                .claims(claims)
                .issuer(issuer)
                .subject(subject)
                .issuedAt(createdDate)
                .id(UUID.randomUUID().toString())
                .expiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .compact();

        return TokenDetailsVo.builder()
                .token(token)
                .issuedAt(createdDate)
                .expiresAt(expirationDate)
                .build();
    }
}
