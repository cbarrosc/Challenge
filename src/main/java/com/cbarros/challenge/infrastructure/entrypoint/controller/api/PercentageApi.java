package com.cbarros.challenge.infrastructure.entrypoint.controller.api;

import com.cbarros.challenge.infrastructure.exception.model.StandardErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@Tag(name = "Percentage", description = "API para calcular porcentajes")
@RequestMapping("/percentage")
public interface PercentageApi {

    @Operation(summary = "Calculo de un porcentaje dinamico", description = "Este endpoint calcula dinamicamente un porcentaje a partir de 2 inputs y un servicio externo que entrega el porcentaje a aplicar.")
    @ApiResponse(responseCode = "200", description = "Porcentaje calculado correctamente")
    @ApiResponse(responseCode = "500", description = "Error al llamar al Servicio")
    @ApiResponse(responseCode = "404", description = "No se pudo obtener el porcentaje ni desde el servicio externo ni desde el caché",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = StandardErrorResponse.class)))
    @GetMapping("/calculatePercentage")
    Mono<Double> calculatePercentage(
            @Parameter(description = "Primer número para el cálculo", example = "10.5")
            @RequestParam double num1,

            @Parameter(description = "Segundo número para el cálculo", example = "20.3")
            @RequestParam double num2
    );
}
