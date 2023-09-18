package com.gavoza.backend.global.exception;

import com.gavoza.backend.global.dto.MessageResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomRuntimeException.class)
    public ResponseEntity<?> handleCustomExceptions(CustomRuntimeException e) {
        return ResponseEntity.status(e.getStatus()).body(new MessageResponseDto(e.getMessage()));
    }
}
