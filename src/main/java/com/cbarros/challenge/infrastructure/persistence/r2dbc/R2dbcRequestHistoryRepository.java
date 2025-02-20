package com.cbarros.challenge.infrastructure.persistence.r2dbc;

import com.cbarros.challenge.domain.model.RequestHistory;
import com.cbarros.challenge.domain.repository.RequestHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class R2dbcRequestHistoryRepository implements RequestHistoryRepository {

    private final R2dbcEntityTemplate template;

    @Override
    public Mono<Void> save(RequestHistory requestHistory) {
        return template.insert(requestHistory).then();
    }
}
