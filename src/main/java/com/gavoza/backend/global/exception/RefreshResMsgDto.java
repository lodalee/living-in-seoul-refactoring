package com.gavoza.backend.global.exception;

import lombok.Getter;

@Getter
public class RefreshResMsgDto {
    private final String msg;
    private final String accessToken;

    public RefreshResMsgDto(String msg, String accessToken) {
        this.msg = msg;
        this.accessToken = accessToken;
    }
}
