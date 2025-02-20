package com.cbarros.challenge.domain.repository;

import com.cbarros.challenge.domain.model.RequestHistory;
import reactor.core.publisher.Mono;

public interface RequestHistoryRepository {
    Mono<Void> save(RequestHistory requestHistory);
}
