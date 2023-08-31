package com.gavoza.backend.domain.user.all.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Date;

@Getter
public class RefreshResMsgDto {
    private final String msg;
    private final String accessToken;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private final Date expirationDate;

    public RefreshResMsgDto(String msg, String accessToken, Date expirationDate) {
        this.msg = msg;
        this.accessToken = accessToken;
        this.expirationDate = expirationDate;
    }
}
