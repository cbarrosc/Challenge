package com.cbarros.challenge.domain.service.interfaces;

import reactor.core.publisher.Mono;

public interface PercentageService {
    Mono<Double> calculatePercentage(double num1, double num2);
}
