package com.cbarros.challenge.domain.service;

import com.cbarros.challenge.domain.model.RequestHistory;
import com.cbarros.challenge.domain.repository.RequestHistoryRepository;
import com.cbarros.challenge.domain.service.interfaces.RequestHistoryService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class RequestHistoryServiceImpl implements RequestHistoryService {

    private final RequestHistoryRepository requestHistoryRepository;

    @Override
    public Mono<Void> saveRequestHistory(String endpoint, String parameters, String response, String error, boolean success) {
        RequestHistory history = RequestHistory.builder()
                .timestamp(LocalDateTime.now())
                .endpoint(endpoint)
                .parameters(parameters)
                .response(response)
                .error(error)
                .success(success)
                .build();

        return requestHistoryRepository.save(history);
    }
}
