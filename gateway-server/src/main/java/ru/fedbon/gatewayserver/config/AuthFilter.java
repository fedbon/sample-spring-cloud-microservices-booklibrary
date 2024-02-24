package ru.fedbon.gatewayserver.config;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.fedbon.gatewayserver.dto.UserValidationResponse;
import ru.fedbon.gatewayserver.exception.InvalidCredentialsException;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static ru.fedbon.gatewayserver.constants.AppConstants.BEARER;
import static ru.fedbon.gatewayserver.constants.ErrorMessage.INVALID_TOKEN;
import static ru.fedbon.gatewayserver.constants.ErrorMessage.MISSING_AUTHORIZATION_HEADER;
import static ru.fedbon.gatewayserver.constants.PathConstants.API_V1_AUTH;
import static ru.fedbon.gatewayserver.constants.PathConstants.AUTH_USERNAME_HEADER;
import static ru.fedbon.gatewayserver.constants.PathConstants.AUTH_USER_ID_HEADER;
import static ru.fedbon.gatewayserver.constants.PathConstants.SIGN_IN;
import static ru.fedbon.gatewayserver.constants.PathConstants.SIGN_UP;
import static ru.fedbon.gatewayserver.constants.PathConstants.VALIDATE_TOKEN;
import static ru.fedbon.gatewayserver.constants.WebClientConstants.USER_SERVER;


@Slf4j
@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final List<String> permittedEndpoints = List.of(
            API_V1_AUTH + SIGN_UP,
            API_V1_AUTH + SIGN_IN
    );

    private final Predicate<ServerHttpRequest> isSecured = request -> permittedEndpoints.stream()
            .noneMatch(uri -> request.getURI().getPath().contains(uri));

    private final WebClient.Builder webClientBuilder;

    public AuthFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (!isSecured.test(request)) {
                return chain.filter(exchange);
            }
            try {
                String token = extractToken(request);
                return webClientBuilder.build()
                        .post()
                        .uri(USER_SERVER + API_V1_AUTH + VALIDATE_TOKEN + token)
                        .retrieve()
                        .bodyToMono(UserValidationResponse.class)
                        .flatMap(userValidationResponse -> {
                            exchange.getRequest()
                                    .mutate()
                                    .header(AUTH_USER_ID_HEADER, userValidationResponse.getId())
                                    .header(AUTH_USERNAME_HEADER, userValidationResponse.getUsername());
                            return chain.filter(exchange);
                        });
            } catch (InvalidCredentialsException e) {
                return Mono.error(new InvalidCredentialsException(INVALID_TOKEN));
            }
        };
    }

    private String extractToken(ServerHttpRequest request) {
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            throw new InvalidCredentialsException(MISSING_AUTHORIZATION_HEADER);
        }
        String authHeader = Objects.requireNonNull(request.getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);
        String[] tokenParts = authHeader.split(" ");
        if (tokenParts.length != 2 || !BEARER.equals(tokenParts[0]) || StringUtils.isEmpty(tokenParts[1])) {
            throw new InvalidCredentialsException(INVALID_TOKEN);
        }
        return tokenParts[1];
    }

    public static class Config {
    }
}

