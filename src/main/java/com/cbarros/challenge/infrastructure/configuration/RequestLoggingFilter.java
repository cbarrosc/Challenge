package com.cbarros.challenge.infrastructure.configuration;

import com.cbarros.challenge.domain.model.RequestHistory;
import com.cbarros.challenge.domain.repository.RequestHistoryRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import org.springframework.lang.NonNull;



@Component
public class RequestLoggingFilter implements WebFilter {

    private final RequestHistoryRepository requestHistoryRepository;

    public RequestLoggingFilter(RequestHistoryRepository requestHistoryRepository) {
        this.requestHistoryRepository = requestHistoryRepository;
    }

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        // Excluir Swagger y OpenAPI
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
            return chain.filter(exchange);
        }

        long startTime = System.currentTimeMillis();

        return chain.filter(exchange)
                .doOnSuccess(response -> saveRequest(exchange, null, true, startTime))
                .doOnError(error -> saveRequest(exchange, error.getMessage(), false, startTime));
    }

    private void saveRequest(ServerWebExchange exchange, String error, boolean success, long startTime) {
        RequestHistory history = RequestHistory.builder()
                .timestamp(LocalDateTime.now())
                .endpoint(exchange.getRequest().getPath().toString())
                .parameters(exchange.getRequest().getQueryParams().toString())
                .response(success ? "Success" : null)
                .error(error)
                .success(success)
                .build();

        requestHistoryRepository.save(history).subscribe(); // 🔹 No bloquea el flujo reactivo
    }
}


