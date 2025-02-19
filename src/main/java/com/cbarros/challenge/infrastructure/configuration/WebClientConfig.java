package com.cbarros.challenge.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient mockServerWebClient(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:1080")
                .defaultHeader("Accept", "application/json").build();
    }
}
