package com.cbarros.challenge.infrastructure.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class PercentageCacheService {
    private final Cache<String, Double> cache;

    public PercentageCacheService() {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();
    }

    public void storePercentage(String key, Double percentage) {
        cache.put(key, percentage);
    }

    public Mono<Double> getPercentage(String key) {
        return Optional.ofNullable(cache.getIfPresent(key))
                .map(Mono::just)
                .orElse(Mono.empty());

    }
}
