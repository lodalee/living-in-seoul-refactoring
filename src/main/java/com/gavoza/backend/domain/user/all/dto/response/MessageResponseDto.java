package com.gavoza.backend.domain.user.all.dto.response;

import lombok.Getter;

@Getter
public class MessageResponseDto {

    private final String message;

    public MessageResponseDto(String message) {
        this.message = message;

    }
}
