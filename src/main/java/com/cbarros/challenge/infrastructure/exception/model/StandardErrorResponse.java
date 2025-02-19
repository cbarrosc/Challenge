package com.cbarros.challenge.infrastructure.exception.model;

import lombok.Builder;

@Builder
public record StandardErrorResponse(String error, String message, int code) { }
