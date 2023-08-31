package com.gavoza.backend.domain.user.all.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequestDto {

    private String refreshToken;
}