package com.gavoza.backend.domain.user.social.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SocialLoginResponseDto {
    private String email;
    private String accessToken;
    private String refreshToken;
    private Date expirationDate;
    private String message;

}

