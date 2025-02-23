package com.cbarros.challenge.infrastructure.entrypoint.controller;

import com.cbarros.challenge.domain.service.interfaces.RequestHistoryService;
import com.cbarros.challenge.infrastructure.entrypoint.controller.model.HistoryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoryControllerTest {

    @Mock
    private RequestHistoryService requestHistoryService;

    @InjectMocks
    private HistoryController historyController;

    @Test
    void getHistory_WhenServiceReturnsData_ShouldReturnOk() {
        HistoryResponse historyResponse = new HistoryResponse(
                1L,
                LocalDateTime.now(),
                "/api/calculate",
                "num1=10&num2=20",
                "{\"result\": 30.0}",
                null,
                true
        );

        when(requestHistoryService.getHistory(any(PageRequest.class)))
                .thenReturn(Flux.just(historyResponse));

        Flux<HistoryResponse> result = historyController.getHistory(0, 10);

        StepVerifier.create(result)
                .expectNext(historyResponse)
                .verifyComplete();
    }

    @Test
    void getHistory_WhenServiceThrowsException_ShouldReturnInternalServerError() {
        when(requestHistoryService.getHistory(any(PageRequest.class)))
                .thenReturn(Flux.error(new RuntimeException("Database error")));

        Flux<HistoryResponse> result = historyController.getHistory(0, 10);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database error"))
                .verify();
    }

    @Test
    void getHistory_WhenRateLimitExceeded_ShouldReturnTooManyRequests() {
        when(requestHistoryService.getHistory(any(PageRequest.class)))
                .thenReturn(Flux.error(WebClientResponseException.create(
                        HttpStatus.TOO_MANY_REQUESTS.value(),
                        "Rate limit exceeded",
                        null, null, null)));

        Flux<HistoryResponse> result = historyController.getHistory(0, 10);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof WebClientResponseException &&
                        ((WebClientResponseException) throwable).getStatusCode() == HttpStatus.TOO_MANY_REQUESTS)
                .verify();
    }
}