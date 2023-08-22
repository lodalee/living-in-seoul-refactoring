package com.gavoza.backend.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequestDto {
    private String nickname;
    private String password;
    private String hometown;
    private String movedDate;
    private String gender;
    private String birthDate;
}

