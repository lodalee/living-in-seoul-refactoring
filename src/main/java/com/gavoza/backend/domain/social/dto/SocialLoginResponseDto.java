package com.gavoza.backend.domain.social.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SocialLoginResponseDto {
    private String nickname;
    private String accessToken;
    private String refreshToken;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private Date expirationDate;
    private String message;
    private boolean hasSignup;

}

