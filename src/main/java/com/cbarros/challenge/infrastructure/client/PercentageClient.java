package com.cbarros.challenge.infrastructure.client;

import com.cbarros.challenge.domain.port.PercentagePort;
import com.cbarros.challenge.infrastructure.configuration.PercentageCacheService;
import com.cbarros.challenge.infrastructure.exception.PercentageServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PercentageClient implements PercentagePort {

    public static final String PERCENTAGE = "percentage";
    public static final String CACHE_ERROR = "Error: No se pudo obtener el porcentaje desde el servicio externo ni desde el caché.";
    private final WebClient mockServerWebClient;
    private final PercentageCacheService cacheService;

    //TODO: Eliminar el map que elimina %

    @Override
    public Mono<Double> getPercentage() {
        return mockServerWebClient.get()
                .uri("/percentage")
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> Double.parseDouble(response.replace("%", "")))
                .doOnNext(percentage -> cacheService.storePercentage(PERCENTAGE, percentage))
                .onErrorResume(error -> cacheService.getPercentage(PERCENTAGE)
                            .switchIfEmpty(Mono.error(new PercentageServiceException(CACHE_ERROR, 404)))
                );
    }
}
