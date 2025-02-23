package com.cbarros.challenge.domain.service;

import com.cbarros.challenge.domain.port.PercentagePort;
import com.cbarros.challenge.infrastructure.configuration.PercentageCacheService;
import com.cbarros.challenge.infrastructure.exception.PercentageServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)  // Inicializa automáticamente los mocks
class PercentageServiceImplTest {

    private static final double NUM1 = 50.0;
    private static final double NUM2 = 30.0;
    private static final double PERCENTAGE = 10.0;
    private static final double EXPECTED_RESULT = 88.0; // (50 + 30) * (1 + (10 / 100)) = 88.0
    private static final String PERCENTAGE_KEY = "percentage";

    @Mock
    private PercentagePort percentagePort;

    @Mock
    private PercentageCacheService cacheService;

    @InjectMocks
    private PercentageServiceImpl percentageService;

    @Test
    void calculatePercentage_WhenServiceSucceeds_ShouldStoreInCacheAndReturnResult() {
        when(percentagePort.getPercentage()).thenReturn(Mono.just(PERCENTAGE));
        when(cacheService.storePercentage(eq(PERCENTAGE_KEY), eq(PERCENTAGE))).thenReturn(Mono.empty());

        Mono<Double> result = percentageService.calculatePercentage(NUM1, NUM2);

        StepVerifier.create(result)
                .expectNext(EXPECTED_RESULT)
                .verifyComplete();

        verify(percentagePort, times(1)).getPercentage();
        verify(cacheService, times(1)).storePercentage(eq(PERCENTAGE_KEY), eq(PERCENTAGE));
        verify(cacheService, never()).getPercentage(anyString());
    }

    @Test
    void calculatePercentage_WhenServiceFailsAndCacheHasValue_ShouldReturnCachedValue() {
        when(percentagePort.getPercentage()).thenReturn(Mono.error(new RuntimeException("Service failed")));
        when(cacheService.getPercentage(eq(PERCENTAGE_KEY))).thenReturn(Mono.just(PERCENTAGE));

        Mono<Double> result = percentageService.calculatePercentage(NUM1, NUM2);

        StepVerifier.create(result)
                .expectNext(EXPECTED_RESULT)
                .verifyComplete();

        verify(percentagePort, times(1)).getPercentage();
        verify(cacheService, times(1)).getPercentage(eq(PERCENTAGE_KEY));
        verify(cacheService, never()).storePercentage(anyString(), anyDouble());
    }

    @Test
    void calculatePercentage_WhenServiceFailsAndCacheIsEmpty_ShouldThrowException() {
        when(percentagePort.getPercentage()).thenReturn(Mono.error(new RuntimeException("Service failed")));
        when(cacheService.getPercentage(eq(PERCENTAGE_KEY))).thenReturn(Mono.empty());

        Mono<Double> result = percentageService.calculatePercentage(NUM1, NUM2);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PercentageServiceException &&
                        ((PercentageServiceException) throwable).getHttpStatusCode() == 404)
                .verify();

        verify(percentagePort, times(1)).getPercentage();
        verify(cacheService, times(1)).getPercentage(eq(PERCENTAGE_KEY));
        verify(cacheService, never()).storePercentage(anyString(), anyDouble());
    }
}