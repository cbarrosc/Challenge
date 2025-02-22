package com.cbarros.challenge.infrastructure.exception;

import com.cbarros.challenge.infrastructure.exception.model.StandardErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PercentageServiceException.class)
    public Mono<ResponseEntity<StandardErrorResponse>> handlePercentageServiceException(PercentageServiceException ex) {
        return Mono.just(ResponseEntity.status(ex.getHttpStatusCode()).body(StandardErrorResponse.builder()
                .error("Error processing percentage")
                .message(ex.getMessage())
                .code(ex.getHttpStatusCode())
                .build()));
    }

    @ExceptionHandler(RateLimitExceedException.class)
        public Mono<ResponseEntity<StandardErrorResponse>> handleRateLimitException2(RateLimitExceedException ex) {
            return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(StandardErrorResponse.builder()
                    .error("Rate limit exceeded")
                    .message(ex.getMessage())
                    .code(ex.getHttpStatusCode()) // 429
                    .build()));
        }
}




