package com.cbarros.challenge.domain.service;

import com.cbarros.challenge.domain.port.PercentagePort;
import com.cbarros.challenge.domain.service.interfaces.PercentageService;
import com.cbarros.challenge.infrastructure.configuration.PercentageCacheService;
import com.cbarros.challenge.infrastructure.exception.PercentageServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PercentageServiceImpl implements PercentageService {

    public static final String PERCENTAGE = "percentage";
    public static final String CACHE_ERROR = "Error: No se pudo obtener el porcentaje desde el servicio externo ni desde el caché.";
    private final PercentagePort percentagePort;
    private final PercentageCacheService cacheService;

    @Override
    public Mono<Double> calculatePercentage(double num1, double num2) {
        return percentagePort.getPercentage()
                .flatMap(percentage -> cacheService.storePercentage(PERCENTAGE, percentage)
                        .thenReturn(percentage))
                .onErrorResume(error -> cacheService.getPercentage(PERCENTAGE)
                        .switchIfEmpty(Mono.error(new PercentageServiceException(CACHE_ERROR, HttpStatus.NOT_FOUND.value()))))
                .map(percentage -> applyPercentage(num1, num2, percentage));
    }

    private double applyPercentage(double num1, double num2, Double percentage){
        return (num1 + num2) * (1 + (percentage / 100));
    }
}
