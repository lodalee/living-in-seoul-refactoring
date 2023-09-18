package com.gavoza.backend.global.dto;

import lombok.Getter;

@Getter
public class MessageResponseDto {

    private final String message;

    public MessageResponseDto(String message) {
        this.message = message;

    }
}
