package com.cbarros.challenge.infrastructure.exception;

import com.cbarros.challenge.infrastructure.exception.model.StandardErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Component
@Order(-2) //mayor prioridad de handling
public class GlobalWebExceptionHandler implements WebExceptionHandler {//se crea porque las excepciones de webfilter no pasan por el otro handler

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (ex instanceof RateLimitExceedException) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            StandardErrorResponse errorResponse = StandardErrorResponse.builder()
                    .error("Rate limit exceeded")
                    .message(ex.getMessage())
                    .code(HttpStatus.TOO_MANY_REQUESTS.value())
                    .build();

            try {
                // Convertir el objeto a JSON
                byte[] jsonBytes = objectMapper.writeValueAsBytes(errorResponse);

                // Escribir el JSON en el cuerpo de la respuesta
                return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(jsonBytes)));
            } catch (Exception e) {
                // Si hay un error al serializar, devolver un error interno del servidor
                exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                return exchange.getResponse().setComplete();
            }
        }

        // Si la excepción no es manejada aquí, propagarla
        return Mono.error(ex);
    }
}