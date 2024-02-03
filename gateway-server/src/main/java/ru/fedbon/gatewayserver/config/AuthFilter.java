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


@Slf4j
@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final List<String> permittedEndpoints = List.of(
            "/api/v1/auth/signup",
            "/api/v1/auth/signin"
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
                        .uri("http://user-server/api/v1/auth/validate?token=" + token)
                        .retrieve()
                        .bodyToMono(UserValidationResponse.class)
                        .flatMap(userValidationResponse -> {
                            exchange.getRequest()
                                    .mutate()
                                    .header("X-auth-user-id", userValidationResponse.getId())
                                    .header("X-auth-username", userValidationResponse.getUsername());
                            return chain.filter(exchange);
                        });
            } catch (InvalidCredentialsException e) {
                return Mono.error(e);
            }
        };
    }

    private String extractToken(ServerHttpRequest request) {
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            throw new InvalidCredentialsException("Missing authorization information");
        }
        String authHeader = Objects.requireNonNull(request.getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);
        String[] tokenParts = authHeader.split(" ");
        if (tokenParts.length != 2 || !"Bearer".equals(tokenParts[0]) || StringUtils.isEmpty(tokenParts[1])) {
            throw new InvalidCredentialsException("Invalid token");
        }
        return tokenParts[1];
    }

    public static class Config {
    }
}

