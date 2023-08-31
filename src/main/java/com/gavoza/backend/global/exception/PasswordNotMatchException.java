package com.gavoza.backend.global.exception;

import org.springframework.http.HttpStatus;

public class PasswordNotMatchException extends CustomRuntimeException {
    public PasswordNotMatchException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
