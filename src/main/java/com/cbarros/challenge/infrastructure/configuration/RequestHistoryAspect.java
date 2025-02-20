package com.cbarros.challenge.infrastructure.configuration;

import com.cbarros.challenge.domain.service.interfaces.RequestHistoryService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Aspect
@Component
public class RequestHistoryAspect {

    private final RequestHistoryService requestHistoryService;

    public RequestHistoryAspect(RequestHistoryService requestHistoryService) {
        this.requestHistoryService = requestHistoryService;
    }

    @Around("@annotation(saveRequestHistory)")
    public Object around(ProceedingJoinPoint joinPoint, SaveRequestHistory saveRequestHistory) throws Throwable {
        Object result = joinPoint.proceed(); // Ejecuta el método original

        if (result instanceof Mono<?>) {
            return ((Mono<?>) result)
                    .flatMap(response -> requestHistoryService.saveRequestHistory(
                            saveRequestHistory.endpoint(),
                            Arrays.toString(joinPoint.getArgs()),
                            response.toString(),
                            null,
                            true
                    ).thenReturn(response))
                    .onErrorResume(error -> {
                        requestHistoryService.saveRequestHistory(
                                saveRequestHistory.endpoint(),
                                Arrays.toString(joinPoint.getArgs()),
                                null,
                                error.getMessage(),
                                false
                        ).subscribe();
                        return Mono.error(error);
                    });
        }

        if (result instanceof Flux<?>) {
            return ((Flux<?>) result)
                    .flatMap(response -> requestHistoryService.saveRequestHistory(
                            saveRequestHistory.endpoint(),
                            Arrays.toString(joinPoint.getArgs()),
                            response.toString(),
                            null,
                            true
                    ).thenReturn(response))
                    .onErrorResume(error -> {
                        requestHistoryService.saveRequestHistory(
                                saveRequestHistory.endpoint(),
                                Arrays.toString(joinPoint.getArgs()),
                                null,
                                error.getMessage(),
                                false
                        ).subscribe();
                        return Flux.error(error);
                    });
        }

        return result;
    }
}


