package com.cbarros.challenge.infrastructure.entrypoint.controller;

import com.cbarros.challenge.domain.service.interfaces.PercentageService;
import com.cbarros.challenge.infrastructure.exception.PercentageServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PercentageControllerTest {

    @Mock
    private PercentageService percentageService;

    @InjectMocks
    private PercentageController percentageController;

    @Test
    void calculatePercentage_WhenServiceSucceeds_ShouldReturnOk() {
        double num1 = 50.0;
        double num2 = 30.0;
        double expectedResult = 88.0; // (50 + 30) * (1 + (10 / 100)) = 88.0

        when(percentageService.calculatePercentage(num1, num2)).thenReturn(Mono.just(expectedResult));

        Mono<Double> result = percentageController.calculatePercentage(num1, num2);
        StepVerifier.create(result)
                .expectNext(expectedResult)
                .verifyComplete();
    }

    @Test
    void calculatePercentage_WhenServiceFailsAndCacheIsEmpty_ShouldReturnNotFound() {
        double num1 = 50.0;
        double num2 = 30.0;

        when(percentageService.calculatePercentage(num1, num2))
                .thenReturn(Mono.error(new PercentageServiceException("Cache is empty", HttpStatus.NOT_FOUND.value())));

        Mono<Double> result = percentageController.calculatePercentage(num1, num2);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PercentageServiceException &&
                        ((PercentageServiceException) throwable).getHttpStatusCode() == HttpStatus.NOT_FOUND.value())
                .verify();
    }

    @Test
    void calculatePercentage_WhenUnhandledErrorOccurs_ShouldReturnInternalServerError() {
        double num1 = 50.0;
        double num2 = 30.0;

        when(percentageService.calculatePercentage(num1, num2))
                .thenReturn(Mono.error(new RuntimeException("Internal server error")));

        Mono<Double> result = percentageController.calculatePercentage(num1, num2);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Internal server error"))
                .verify();
    }

    @Test
    void calculatePercentage_WhenRateLimitExceeded_ShouldReturnTooManyRequests() {
        double num1 = 50.0;
        double num2 = 30.0;

        when(percentageService.calculatePercentage(num1, num2))
                .thenReturn(Mono.error(WebClientResponseException.create(
                        HttpStatus.TOO_MANY_REQUESTS.value(),
                        "Rate limit exceeded",
                        null, null, null)));

        Mono<Double> result = percentageController.calculatePercentage(num1, num2);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof WebClientResponseException &&
                        ((WebClientResponseException) throwable).getStatusCode() == HttpStatus.TOO_MANY_REQUESTS)
                .verify();
    }
}