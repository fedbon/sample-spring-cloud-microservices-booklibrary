package ru.fedbon.gatewayservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import ru.fedbon.gatewayservice.dto.UserDto;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;


@Slf4j
@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final List<String> permittedEndpoints = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/login"
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
        log.debug("AuthenticationPrefilter applied for route: {}", config);
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (isSecured.test(request)) {
                String token = extractToken(request);

                return webClientBuilder.build()
                        .post()
                        .uri("lb://security-service/api/v1/auth/validate-token?token=" + token)
                        .retrieve()
                        .bodyToMono(UserDto.class)
                        .flatMap(userDto -> {
                            exchange.getRequest().mutate()
                                    .header("X-auth-user-id", String.valueOf(userDto.getId()));
                            return chain.filter(exchange);
                        });
            } else {
                return chain.filter(exchange);
            }
        };
    }

    private String extractToken(ServerHttpRequest request) {
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            throw new RuntimeException("Missing authorization information");
        }
        String authHeader = Objects.requireNonNull(request.getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);
        String[] tokenParts = authHeader.split(" ");
        if (tokenParts.length != 2 || !"Bearer".equals(tokenParts[0])) {
            throw new RuntimeException("Incorrect authorization structure");
        }
        return tokenParts[1];
    }

    public static class Config {
    }
}

