package com.cbarros.challenge.infrastructure.configuration;

import com.cbarros.challenge.infrastructure.exception.RateLimitExceedException;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class RateLimiterWebFilter implements WebFilter {

    private final RedisRateLimiter redisRateLimiter;
    public static final String RATE_LIMIT_EXCEEDED = "Se ha excedido el limite de llamadas permitidas por minuto";


    public RateLimiterWebFilter(RedisRateLimiter redisRateLimiter) {
        this.redisRateLimiter = redisRateLimiter;
    }

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Excluir las rutas de Swagger
        if (path.startsWith("/swagger") || path.startsWith("/v3/api-docs") || path.startsWith("/v2/api-docs") || path.startsWith("/webjars")) {
            return chain.filter(exchange);
        }

        // Aplicar el Rate Limiter solo en otras rutas
        String key = "rateLimiterKey"; // Puedes usar el IP del cliente o un identificador único
        int maxRequests = 3; // Máximo de solicitudes permitidas
        Duration duration = Duration.ofMinutes(1); // Período de tiempo

        if (redisRateLimiter.allowRequest(key, maxRequests, duration)) {
            return chain.filter(exchange); // Permitir la solicitud
        } else {
            // Lanzar excepción personalizada
            return Mono.error(new RateLimitExceedException(RATE_LIMIT_EXCEEDED, HttpStatus.TOO_MANY_REQUESTS.value()));
        }
    }
}