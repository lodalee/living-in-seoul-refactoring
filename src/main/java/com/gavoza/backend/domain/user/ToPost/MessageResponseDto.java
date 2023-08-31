package com.gavoza.backend.domain.user.ToPost;

import lombok.Getter;

@Getter
public class MessageResponseDto {

    private final String message;

    public MessageResponseDto(String message) {
        this.message = message;

    }
}
