package ru.fedbon.securityservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.fedbon.securityservice.dto.UserValidationResponse;
import ru.fedbon.securityservice.exception.UnauthorizedException;
import ru.fedbon.securityservice.model.User;
import ru.fedbon.securityservice.exception.AuthException;
import ru.fedbon.securityservice.dto.security.TokenDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Integer expirationInSeconds;

    @Value("${jwt.issuer}")
    private String issuer;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public Mono<TokenDetails> authenticate(String username, String password) {
        return userService.getUserByUsername(username)
                .flatMap(user -> {
                    if (!user.isEnabled()) {
                        return Mono.error(new AuthException("Account disabled", "FEDBON_USER_ACCOUNT_DISABLED"));
                    }

                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        return Mono.error(new AuthException("Invalid password", "FEDBON_INVALID_PASSWORD"));
                    }

                    return Mono.just(generateToken(user).toBuilder()
                            .userId(user.getId())
                            .build());
                })
                .switchIfEmpty(Mono.error(new AuthException("Invalid username", "FEDBON_INVALID_USERNAME")));
    }

    public Mono<UserValidationResponse> validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String userId = claims.getSubject();
            return userService.getUserById(Long.parseLong(userId))
                    .map(user -> UserValidationResponse.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .role(user.getRole())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .token(token)
                            .build());
        } catch (ExpiredJwtException e) {
            log.warn("Expired token: {}", e.getMessage());
            return Mono.error(e);
        } catch (JwtException e) {
            log.error("Invalid token: {}", e.getMessage());
            return Mono.error(new UnauthorizedException("Invalid token"));
        }
    }

    private TokenDetails generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
            claims.put("role", user.getRole());
            claims.put("username", user.getUsername());
        return generateToken(claims, user.getId().toString());
    }

    private TokenDetails generateToken(Map<String, Object> claims, String subject) {
        long expirationTimeInMillis = expirationInSeconds * 1000L;
        Date expirationDate = new Date(new Date().getTime() + expirationTimeInMillis);

        return generateToken(expirationDate, claims, subject);
    }

    private TokenDetails generateToken(Date expirationDate, Map<String, Object> claims, String subject) {
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

        return TokenDetails.builder()
                .token(token)
                .issuedAt(createdDate)
                .expiresAt(expirationDate)
                .build();
    }
}
