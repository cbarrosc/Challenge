package com.cbarros.challenge.infrastructure.entrypoint.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Respuesta del historial de solicitudes")
public record HistoryResponse(
        @Schema(description = "ID del registro", example = "1")
        Long id,

        @Schema(description = "Fecha y hora de la solicitud", example = "2023-10-01T12:34:56")
        LocalDateTime timestamp,

        @Schema(description = "Endpoint invocado", example = "/percentage")
        String endpoint,

        @Schema(description = "Parámetros recibidos", example = "{}")
        String parameters,

        @Schema(description = "Respuesta del servicio", example = "Success")
        String response,

        @Schema(description = "Error ocurrido", example = "Error: No se pudo obtener el porcentaje")
        String error,

        @Schema(description = "Indica si la solicitud fue exitosa", example = "true")
        boolean success
) {}
