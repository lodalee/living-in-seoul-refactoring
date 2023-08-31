package com.gavoza.backend.global.exception;

import org.springframework.http.HttpStatus;

public class EmailNotFoundException extends CustomRuntimeException {
    public EmailNotFoundException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
