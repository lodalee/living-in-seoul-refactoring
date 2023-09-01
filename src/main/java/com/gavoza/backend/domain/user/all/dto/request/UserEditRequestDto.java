package com.gavoza.backend.domain.user.all.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEditRequestDto {
    private String nickname;
    private String password;
    private String hometown;
    private String movedDate;
    private String gender;
    private String birthDate;
}

