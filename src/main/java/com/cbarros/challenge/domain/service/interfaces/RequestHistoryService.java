package com.cbarros.challenge.domain.service.interfaces;

import reactor.core.publisher.Mono;

public interface RequestHistoryService {

    Mono<Void> saveRequestHistory(String endpoint, String parameters, String response, String error, boolean success);

}
