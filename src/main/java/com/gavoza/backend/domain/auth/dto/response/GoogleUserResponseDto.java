package com.gavoza.backend.domain.auth.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleUserResponseDto {
    private String id;
    private String email;
    private String verified_email;
    private String name;
    private String given_name;
    private String family_name;
}
