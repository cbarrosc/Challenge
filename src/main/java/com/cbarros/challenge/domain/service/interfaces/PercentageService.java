package com.cbarros.challenge.domain.service.interfaces;

import com.cbarros.challenge.infrastructure.entrypoint.controller.model.PercentageRequest;
import reactor.core.publisher.Mono;

public interface PercentageService {
    Mono<Double> calculatePercentage(PercentageRequest request);
}
