package com.gavoza.backend.domain.user.all.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Date;

@Getter
public class TokenResMsgDto {
    private final String nickname;
    private final String msg;
    private final String accessToken;
    private final String refreshToken;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private final Date expirationDate;

    public TokenResMsgDto(String nickname, String msg, String accessToken, String refreshToken, Date expirationDate) {
        this.nickname = nickname;
        this.msg = msg;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expirationDate = expirationDate;
    }
}
