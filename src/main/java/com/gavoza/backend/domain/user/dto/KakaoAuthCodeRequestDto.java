package com.gavoza.backend.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoAuthCodeRequestDto {
    private String authCode;

    public String getAuthCode() {

        return authCode;
    }

}
