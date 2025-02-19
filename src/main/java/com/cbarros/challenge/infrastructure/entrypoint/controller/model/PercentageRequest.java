package com.cbarros.challenge.infrastructure.entrypoint.controller.model;

import org.springframework.web.bind.annotation.RequestParam;

public record PercentageRequest(
        @RequestParam double num1,
        @RequestParam double num2) {
}
