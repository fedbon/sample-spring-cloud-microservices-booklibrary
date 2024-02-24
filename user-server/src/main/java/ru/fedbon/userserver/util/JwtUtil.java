package ru.fedbon.userserver.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.fedbon.userserver.exception.InvalidCredentialsException;
import ru.fedbon.userserver.vo.VerificationResultVo;
import reactor.core.publisher.Mono;

import java.util.Date;

import static ru.fedbon.userserver.constants.ErrorMessage.TOKEN_EXPIRED;
import static ru.fedbon.userserver.constants.ErrorMessage.TOKEN_EXPIRED_WITH_MESSAGE;

@Slf4j
@RequiredArgsConstructor
public class JwtUtil {

    private final String secret;

    public Mono<VerificationResultVo> getVerificationResult(String accessToken) {
        try {
            Claims claims = getClaimsFromToken(accessToken);
            Date expirationDate = claims.getExpiration();

            if (expirationDate != null && expirationDate.before(new Date())) {
                log.warn(TOKEN_EXPIRED);
                return Mono.error(new ExpiredJwtException(null, claims, TOKEN_EXPIRED));
            } else {
                return Mono.just(new VerificationResultVo(claims, accessToken));
            }
        } catch (ExpiredJwtException e) {
            log.warn(TOKEN_EXPIRED_WITH_MESSAGE, e.getMessage());
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

