package com.cbarros.challenge.infrastructure.entrypoint.controller.api;

import com.cbarros.challenge.infrastructure.entrypoint.controller.model.HistoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

@Tag(name = "History", description = "API para consultar el historial de solicitudes")
@RequestMapping("/history")
public interface HistoryApi {

    @Operation(
            summary = "Obtener historial de solicitudes",
            description = "Retorna un flujo de historial de solicitudes con soporte para paginación."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Historial obtenido exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = HistoryResponse.class) // Usamos HistoryResponse como esquema
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros de paginación inválidos",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @GetMapping
    Flux<HistoryResponse> getHistory(
            @Parameter(description = "Número de la página (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Tamaño de la página", example = "10")
            @RequestParam(defaultValue = "10") int size
    );
}