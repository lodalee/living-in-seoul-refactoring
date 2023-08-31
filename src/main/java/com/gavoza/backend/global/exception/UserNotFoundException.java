package com.gavoza.backend.global.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends CustomRuntimeException {
    public UserNotFoundException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

