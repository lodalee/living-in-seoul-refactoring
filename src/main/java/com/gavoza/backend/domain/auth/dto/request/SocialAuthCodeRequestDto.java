package com.gavoza.backend.domain.auth.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialAuthCodeRequestDto {
    private String authCode;
}
