package ru.fedbon.securityservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.fedbon.securityservice.exception.UnauthorizedException;
import ru.fedbon.securityservice.dto.security.ValidationResult;
import reactor.core.publisher.Mono;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class JwtUtil {

    private final String secret;

    public Mono<ValidationResult> getValidationResult(String accessToken) {
        try {
            Claims claims = getClaimsFromToken(accessToken);
            boolean isTokenExpired = isTokenExpired(claims);

            if (isTokenExpired) {
                return Mono.just(new ValidationResult(claims, accessToken));
            } else {
                throw new UnauthorizedException("Invalid token!");
            }
        } catch (ExpiredJwtException e) {
            log.warn("Expired token: {}", e.getMessage());
            return Mono.error(e);
        } catch (UnauthorizedException e) {
            return Mono.error(e);
        }
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(Claims claims) {
        Date expirationDate = claims.getExpiration();
        return !expirationDate.before(new Date());
    }
}

