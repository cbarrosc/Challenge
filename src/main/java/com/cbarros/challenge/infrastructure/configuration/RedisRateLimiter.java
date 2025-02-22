package com.cbarros.challenge.infrastructure.configuration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class RedisRateLimiter {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisRateLimiter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean allowRequest(String key, int maxRequests, Duration duration) {
        Long currentRequests = redisTemplate.opsForValue().increment(key);
        if (currentRequests == 1) {
            // Establecer el tiempo de expiración para la clave
            redisTemplate.expire(key, duration.toMillis(), TimeUnit.MILLISECONDS);
        }
        return currentRequests != null && currentRequests <= maxRequests;
    }
}
