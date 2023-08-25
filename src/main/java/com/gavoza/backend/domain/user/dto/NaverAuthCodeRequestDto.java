package com.gavoza.backend.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverAuthCodeRequestDto {
    private String authCode;
}
