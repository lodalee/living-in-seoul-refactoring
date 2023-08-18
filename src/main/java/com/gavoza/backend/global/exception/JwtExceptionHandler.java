package com.gavoza.backend.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class JwtExceptionHandler {
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<String> handleJwtException(JwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getErrorMessage());
    }
}

