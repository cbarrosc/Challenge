package com.cbarros.challenge.infrastructure.client;

import com.cbarros.challenge.domain.port.PercentagePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PercentageClient implements PercentagePort {

    private final WebClient mockServerWebClient;

    @Override
    public Mono<Double> getPercentage() {
        return mockServerWebClient.get()
                .uri("/percentage")
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> Double.parseDouble(response.replace("%", "")));
    }
}
