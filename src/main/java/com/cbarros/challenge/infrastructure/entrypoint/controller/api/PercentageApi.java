package com.cbarros.challenge.infrastructure.entrypoint.controller.api;

import com.cbarros.challenge.infrastructure.entrypoint.controller.model.PercentageRequest;
import com.cbarros.challenge.infrastructure.exception.model.StandardErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

public interface PercentageApi {

    @Operation(summary = "Calculo de un porcentaje dinamico", description = "Este endpoint calcula dinamicamente un porcentaje a partir de 2 inputs y un servicio externo que entrega el porcentaje a aplicar.")
    @ApiResponse(responseCode = "200", description = "Porcentaje calculado correctamente")
    @ApiResponse(responseCode = "500", description = "Error al llamar al Servicio")
    @ApiResponse(responseCode = "404", description = "No se pudo obtener el porcentaje ni desde el servicio externo ni desde el caché",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = StandardErrorResponse.class)))
    @GetMapping("/calculatePercentage")
    Mono<Double> calculatePercentage(PercentageRequest percentageRequest);
}
