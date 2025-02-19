package com.cbarros.challenge.infrastructure.entrypoint.controller.api;

import com.cbarros.challenge.infrastructure.entrypoint.controller.model.PercentageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

public interface PercentageApi {

    @Operation(summary = "Calculo de un porcentaje dinamico", description = "Este endpoint calcula dinamicamente un porcentaje a partir de 2 inputs y un servicio externo que entrega el porcentaje a aplicar.")
    @ApiResponse(responseCode = "200", description = "Porcentaje calculado correctamente")
    @ApiResponse(responseCode = "500", description = "Error al llamar al Servicio")
    @GetMapping("/calculatePercentage")
    Mono<Double> calculatePercentage(PercentageRequest percentageRequest);
}
