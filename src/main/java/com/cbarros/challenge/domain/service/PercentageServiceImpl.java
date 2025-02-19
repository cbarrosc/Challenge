package com.cbarros.challenge.domain.service;

import com.cbarros.challenge.domain.port.PercentagePort;
import com.cbarros.challenge.domain.service.interfaces.PercentageService;
import com.cbarros.challenge.infrastructure.entrypoint.controller.model.PercentageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PercentageServiceImpl implements PercentageService {

    private final PercentagePort percentagePort;

    @Override
    public Mono<Double> calculatePercentage(PercentageRequest percentageRequest) {
        return percentagePort
                .getPercentage()
                .map(percentage -> applyPercentage(percentageRequest, percentage));
    }

    private double applyPercentage(PercentageRequest percentageRequest, Double percentage){
        return (percentageRequest.num1() + percentageRequest.num2()) * (1 + (percentage / 100));
    }
}
