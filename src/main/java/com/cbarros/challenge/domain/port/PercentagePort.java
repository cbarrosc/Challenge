package com.cbarros.challenge.domain.port;

import reactor.core.publisher.Mono;

public interface PercentagePort {
    Mono <Double> getPercentage();
}
