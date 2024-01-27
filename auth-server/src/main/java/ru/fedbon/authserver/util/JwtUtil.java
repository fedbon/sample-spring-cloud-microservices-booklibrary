package ru.fedbon.authserver.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.fedbon.authserver.exception.InvalidCredentialsException;
import ru.fedbon.authserver.vo.VerificationResultVo;
import reactor.core.publisher.Mono;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class JwtUtil {

    private final String secret;

    public Mono<VerificationResultVo> getVerificationResult(String accessToken) {
        try {
            Claims claims = getClaimsFromToken(accessToken);
            Date expirationDate = claims.getExpiration();

            if (expirationDate != null && expirationDate.before(new Date())) {
                log.warn("Expired token");
                return Mono.error(new ExpiredJwtException(null, claims, "Token has expired"));
            } else {
                return Mono.just(new VerificationResultVo(claims, accessToken));
            }
        } catch (ExpiredJwtException e) {
            log.warn("Expired token: {}", e.getMessage());
            return Mono.error(e);
        } catch (InvalidCredentialsException e) {
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
}

