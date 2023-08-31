package com.gavoza.backend.domain.user.social.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialLoginResponseDto {
    private String email;
    private String accessToken;
    private String refreshToken;
    private long expirationTime;
    private String message;

}

