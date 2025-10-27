package com.sji.microservice.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class EmployeeCourrierFilter extends AbstractGatewayFilterFactory<EmployeeCourrierFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public EmployeeCourrierFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
            } else {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            try {
                if (!jwtUtil.validateToken(authHeader)) {
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }

                String userRole = jwtUtil.extractRole(authHeader);
                if (!"EMPLOYE".equals(userRole)) {
                    return onError(exchange, HttpStatus.FORBIDDEN);
                }

                // Add user ID to request headers for backend validation
                String userId = jwtUtil.extractUserId(authHeader);
                ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", userRole)
                    .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    public static class Config {
        // Configuration properties if needed
    }
}