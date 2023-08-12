package com.gavoza.backend.global.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class MessageResponseDto {
    private String message;

    public MessageResponseDto(String message) {
        this.message = message;
    }
}
