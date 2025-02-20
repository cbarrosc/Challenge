package com.cbarros.challenge.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient mockServerWebClient(WebClient.Builder builder, MockServerProperties properties) {
        Retry retry = Retry.fixedDelay(3, Duration.ofSeconds(2)) //se configuran la cantidad de reintentos y cada cuanto tiempo
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                    throw new RuntimeException("Retry exhausted after " + retrySignal.totalRetries() + " attempts");
                });

        return builder.baseUrl(properties.getBaseUrl())
                .defaultHeader("Accept", "application/json")
                .filter((request, next) -> next.exchange(request)
                        .flatMap(clientResponse -> {
                            if (clientResponse.statusCode().is5xxServerError()) {
                                return clientResponse.createException()
                                        .flatMap(Mono::error);
                            }
                            return Mono.just(clientResponse);
                        })
                        .retryWhen(retry))
                .build();
    }
}