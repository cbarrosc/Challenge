package com.cbarros.challenge.infrastructure.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class PercentageServiceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    private final int httpStatusCode;

    public PercentageServiceException(String message, int httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public PercentageServiceException(String message, Throwable cause, int httpStatusCode) {
        super(message, cause);
        this.httpStatusCode = httpStatusCode;
    }

}

