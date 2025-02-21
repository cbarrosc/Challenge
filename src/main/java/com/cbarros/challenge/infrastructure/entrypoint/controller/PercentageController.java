package com.cbarros.challenge.infrastructure.entrypoint.controller;

import com.cbarros.challenge.domain.service.interfaces.PercentageService;
import com.cbarros.challenge.infrastructure.configuration.SaveRequestHistory;
import com.cbarros.challenge.infrastructure.entrypoint.controller.api.PercentageApi;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class PercentageController implements PercentageApi {

    private final PercentageService percentageService;

    @Override
    @SaveRequestHistory(endpoint = "/calculatePercentage")
    public Mono<Double> calculatePercentage(double num1, double num2) {
        return percentageService.calculatePercentage(num1, num2);
    }

}
