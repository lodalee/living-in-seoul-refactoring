package com.gavoza.backend.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoLoginResponseDto {
    private String email;
    private String accessToken;
    private String refreshToken;

}

