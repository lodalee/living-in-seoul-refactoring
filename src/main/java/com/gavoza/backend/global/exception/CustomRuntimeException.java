package com.gavoza.backend.global.exception;

import org.springframework.http.HttpStatus;

public class CustomRuntimeException extends RuntimeException {
    private final HttpStatus status;

    public CustomRuntimeException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public CustomRuntimeException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
