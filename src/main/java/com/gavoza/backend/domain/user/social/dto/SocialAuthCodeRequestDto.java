package com.gavoza.backend.domain.user.social.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialAuthCodeRequestDto {
    private String authCode;
}
