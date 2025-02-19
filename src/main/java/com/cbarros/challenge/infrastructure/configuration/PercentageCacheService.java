package com.cbarros.challenge.infrastructure.configuration;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class PercentageCacheService {
    private static final Duration TTL = Duration.ofMinutes(30);
    private final ReactiveStringRedisTemplate redisTemplate;
    private final ReactiveValueOperations<String, String> valueOps;

    public PercentageCacheService(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOps = redisTemplate.opsForValue();
    }

    public Mono<Void> storePercentage(String key, Double percentage) {
        return valueOps.set(key, percentage.toString(), TTL).then();
    }

    public Mono<Double> getPercentage(String key) {
        return valueOps.get(key)
                .map(Double::parseDouble)
                .switchIfEmpty(Mono.empty());
    }
}
