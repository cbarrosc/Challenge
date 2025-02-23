package com.cbarros.challenge.domain.service;

import com.cbarros.challenge.domain.model.RequestHistory;
import com.cbarros.challenge.domain.repository.RequestHistoryRepository;
import com.cbarros.challenge.infrastructure.entrypoint.controller.model.HistoryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestHistoryServiceImplTest {

    @Mock
    private RequestHistoryRepository requestHistoryRepository;

    @InjectMocks
    private RequestHistoryServiceImpl requestHistoryService;

    @Test
    void getHistory_WhenRepositoryReturnsData_ShouldMapToHistoryResponse() {
        RequestHistory historyEntity = new RequestHistory(
               1L,
                LocalDateTime.now(),
                "/api/calculate",
                "num1=10&num2=20",
                "{\"result\": 30.0}",
                null,
                true
        );

        when(requestHistoryRepository.findAllBy(any(Pageable.class)))
                .thenReturn(Flux.just(historyEntity));

        Flux<HistoryResponse> result = requestHistoryService.getHistory(Pageable.unpaged());

        StepVerifier.create(result)
                .expectNextMatches(historyResponse ->
                        historyResponse.id().equals(historyEntity.getId()) &&
                                historyResponse.timestamp().equals(historyEntity.getTimestamp()) &&
                                historyResponse.endpoint().equals(historyEntity.getEndpoint()) &&
                                historyResponse.parameters().equals(historyEntity.getParameters()) &&
                                historyResponse.response().equals(historyEntity.getResponse()) &&
                                historyResponse.error() == null &&
                                historyResponse.success() == historyEntity.isSuccess()
                )
                .verifyComplete();
    }

    @Test
    void getHistory_WhenRepositoryReturnsEmpty_ShouldReturnEmptyFlux() {
        when(requestHistoryRepository.findAllBy(any(Pageable.class)))
                .thenReturn(Flux.empty());

        Flux<HistoryResponse> result = requestHistoryService.getHistory(Pageable.unpaged());

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void getHistory_WhenRepositoryThrowsException_ShouldPropagateError() {
        when(requestHistoryRepository.findAllBy(any(Pageable.class)))
                .thenReturn(Flux.error(new RuntimeException("Database error")));

        Flux<HistoryResponse> result = requestHistoryService.getHistory(Pageable.unpaged());

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database error"))
                .verify();
    }
}