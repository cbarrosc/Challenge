package com.cbarros.challenge.domain.service;

import com.cbarros.challenge.domain.port.PercentagePort;
import com.cbarros.challenge.domain.service.interfaces.PercentageService;
import com.cbarros.challenge.infrastructure.configuration.PercentageCacheService;
import com.cbarros.challenge.infrastructure.entrypoint.controller.model.PercentageRequest;
import com.cbarros.challenge.infrastructure.exception.PercentageServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PercentageServiceImpl implements PercentageService {

    public static final String PERCENTAGE = "percentage";
    public static final String CACHE_ERROR = "Error: No se pudo obtener el porcentaje desde el servicio externo ni desde el caché.";
    public static final int NOT_FOUND = 404;
    private final PercentagePort percentagePort;
    private final PercentageCacheService cacheService;

    @Override
    public Mono<Double> calculatePercentage(PercentageRequest percentageRequest) {
        return percentagePort.getPercentage()
                .flatMap(percentage -> cacheService.storePercentage(PERCENTAGE, percentage)
                        .thenReturn(percentage))
                .onErrorResume(error -> cacheService.getPercentage(PERCENTAGE)
                        .switchIfEmpty(Mono.error(new PercentageServiceException(CACHE_ERROR, NOT_FOUND))))
                .map(percentage -> applyPercentage(percentageRequest, percentage));
    }

    private double applyPercentage(PercentageRequest percentageRequest, Double percentage){
        return (percentageRequest.num1() + percentageRequest.num2()) * (1 + (percentage / 100));
    }
}
