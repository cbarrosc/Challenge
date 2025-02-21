package com.cbarros.challenge.domain.service.interfaces;

import com.cbarros.challenge.infrastructure.entrypoint.controller.model.HistoryResponse;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;

public interface RequestHistoryService {
    Flux<HistoryResponse> getHistory(Pageable pageable);
}
