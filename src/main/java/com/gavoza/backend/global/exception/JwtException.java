package com.gavoza.backend.global.exception;

import org.springframework.http.HttpStatus;

public class JwtException extends CustomRuntimeException {
    public JwtException(String errorMessage) {
        super(errorMessage, HttpStatus.UNAUTHORIZED);
    }
}
