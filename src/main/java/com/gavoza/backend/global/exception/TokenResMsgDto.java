package com.gavoza.backend.global.exception;

import lombok.Getter;

@Getter
public class TokenResMsgDto {
    private final String msg;
    private final String accessToken;
    private final String refreshToken;

    public TokenResMsgDto(String msg, String accessToken, String refreshToken) {
        this.msg = msg;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
