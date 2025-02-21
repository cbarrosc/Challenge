package com.cbarros.challenge.domain.service;

import com.cbarros.challenge.domain.repository.RequestHistoryRepository;
import com.cbarros.challenge.domain.service.interfaces.RequestHistoryService;
import com.cbarros.challenge.infrastructure.entrypoint.controller.model.HistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class RequestHistoryServiceImpl implements RequestHistoryService {

    private final RequestHistoryRepository requestHistoryRepository;


        @Override
        public Flux<HistoryResponse> getHistory(Pageable pageable) {
            return requestHistoryRepository.findAllBy(pageable)
                    .map(history -> new HistoryResponse(
                            history.getId(),
                            history.getTimestamp(),
                            history.getEndpoint(),
                            history.getParameters(),
                            history.getResponse(),
                            history.getError(),
                            history.isSuccess()
                    ));
        }
}
