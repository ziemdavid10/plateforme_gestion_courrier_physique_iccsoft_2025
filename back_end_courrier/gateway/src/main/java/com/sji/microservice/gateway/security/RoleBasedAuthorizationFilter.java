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
public class RoleBasedAuthorizationFilter extends AbstractGatewayFilterFactory<RoleBasedAuthorizationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public RoleBasedAuthorizationFilter() {
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

                if (config.getRequiredRole() != null) {
                    String[] requiredRoles = config.getRequiredRole().split(",");
                    boolean hasRequiredRole = false;
                    for (String role : requiredRoles) {
                        if (jwtUtil.hasRole(authHeader, role.trim())) {
                            hasRequiredRole = true;
                            break;
                        }
                    }
                    if (!hasRequiredRole) {
                        return onError(exchange, HttpStatus.FORBIDDEN);
                    }
                }
            } catch (Exception e) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    public static class Config {
        private String requiredRole;

        public String getRequiredRole() {
            return requiredRole;
        }

        public void setRequiredRole(String requiredRole) {
            this.requiredRole = requiredRole;
        }
    }
}