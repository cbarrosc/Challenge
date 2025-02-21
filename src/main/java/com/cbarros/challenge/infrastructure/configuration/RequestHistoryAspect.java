package com.cbarros.challenge.infrastructure.configuration;

import com.cbarros.challenge.domain.model.RequestHistory;
import com.cbarros.challenge.domain.repository.RequestHistoryRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class RequestHistoryAspect {

    private final RequestHistoryRepository requestHistoryRepository;

    public RequestHistoryAspect(RequestHistoryRepository requestHistoryRepository) {
        this.requestHistoryRepository = requestHistoryRepository;
    }

    @Around("@annotation(saveRequestHistory)")
    public Object around(ProceedingJoinPoint joinPoint, SaveRequestHistory saveRequestHistory) throws Throwable {
        Object result = joinPoint.proceed(); // Ejecuta el método original

        if (result instanceof Mono<?>) {
            return ((Mono<?>) result)
                    .flatMap(response -> {
                        // Crear y guardar el historial en caso de éxito
                        RequestHistory history = new RequestHistory();
                        history.setTimestamp(LocalDateTime.now());
                        history.setEndpoint(saveRequestHistory.endpoint());
                        history.setParameters(Arrays.toString(joinPoint.getArgs()));
                        history.setResponse(response.toString());
                        history.setError(null);
                        history.setSuccess(true);

                        return requestHistoryRepository.save(history).thenReturn(response);
                    })
                    .onErrorResume(error -> {
                        // Crear y guardar el historial en caso de error
                        RequestHistory history = new RequestHistory();
                        history.setTimestamp(LocalDateTime.now());
                        history.setEndpoint(saveRequestHistory.endpoint());
                        history.setParameters(Arrays.toString(joinPoint.getArgs()));
                        history.setResponse(null);
                        history.setError(error.getMessage());
                        history.setSuccess(false);

                        return requestHistoryRepository.save(history).then(Mono.error(error));
                    });
        }

        if (result instanceof Flux<?>) {
            return ((Flux<?>) result)
                    .flatMap(response -> {
                        // Crear y guardar el historial en caso de éxito
                        RequestHistory history = new RequestHistory();
                        history.setTimestamp(LocalDateTime.now());
                        history.setEndpoint(saveRequestHistory.endpoint());
                        history.setParameters(Arrays.toString(joinPoint.getArgs()));
                        history.setResponse(response.toString());
                        history.setError(null);
                        history.setSuccess(true);

                        return requestHistoryRepository.save(history).thenReturn(response);
                    })
                    .onErrorResume(error -> {
                        // Crear y guardar el historial en caso de error
                        RequestHistory history = new RequestHistory();
                        history.setTimestamp(LocalDateTime.now());
                        history.setEndpoint(saveRequestHistory.endpoint());
                        history.setParameters(Arrays.toString(joinPoint.getArgs()));
                        history.setResponse(null);
                        history.setError(error.getMessage());
                        history.setSuccess(false);

                        return requestHistoryRepository.save(history).thenMany(Flux.error(error));
                    });
        }

        return result;
    }
}