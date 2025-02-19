package com.cbarros.challenge.infrastructure.entrypoint.controller;

import com.cbarros.challenge.domain.service.interfaces.PercentageService;
import com.cbarros.challenge.infrastructure.entrypoint.controller.api.PercentageApi;
import com.cbarros.challenge.infrastructure.entrypoint.controller.model.PercentageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class PercentageController implements PercentageApi {

    private final PercentageService percentageService;

    //http://localhost:8080/swagger-ui/index.html
    @Override
    public Mono<Double> calculatePercentage(PercentageRequest percentageRequest) {

        return percentageService.calculatePercentage(percentageRequest);
    }

}
