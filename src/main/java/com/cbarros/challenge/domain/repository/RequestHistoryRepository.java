package com.cbarros.challenge.domain.repository;

import com.cbarros.challenge.domain.model.RequestHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface RequestHistoryRepository extends ReactiveCrudRepository<RequestHistory, Long> {
    Flux<RequestHistory> findAllBy(Pageable pageable);
}
