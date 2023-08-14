package com.gavoza.backend.domain.user.dto;

import lombok.Getter;

@Getter
public class TokenResponseDto {

    private final String message;
    private final String accessToken;
    private final String refreshToken;

    public TokenResponseDto(String message, String accessToken, String refreshToken) {
        this.message = message;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
