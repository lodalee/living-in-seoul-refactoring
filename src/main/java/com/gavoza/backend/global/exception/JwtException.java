package com.gavoza.backend.global.exception;

public class JwtException extends RuntimeException {
    private String errorMessage;

    public JwtException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
