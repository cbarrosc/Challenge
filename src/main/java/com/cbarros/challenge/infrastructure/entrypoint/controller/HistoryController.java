package com.cbarros.challenge.infrastructure.entrypoint.controller;

import com.cbarros.challenge.domain.service.interfaces.RequestHistoryService;
import com.cbarros.challenge.infrastructure.entrypoint.controller.api.HistoryApi;
import com.cbarros.challenge.infrastructure.entrypoint.controller.model.HistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class HistoryController implements HistoryApi {

    private final RequestHistoryService requestHistoryService;

    @Override
    public Flux<HistoryResponse> getHistory(int page, int size) {
        return requestHistoryService.getHistory(PageRequest.of(page, size));
    }
}
