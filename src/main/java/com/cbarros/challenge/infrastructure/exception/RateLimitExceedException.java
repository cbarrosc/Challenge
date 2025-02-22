package com.cbarros.challenge.infrastructure.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class RateLimitExceedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    private final int httpStatusCode;

    public RateLimitExceedException(String message, int httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public RateLimitExceedException(String message, Throwable cause, int httpStatusCode) {
        super(message, cause);
        this.httpStatusCode = httpStatusCode;
    }

}

