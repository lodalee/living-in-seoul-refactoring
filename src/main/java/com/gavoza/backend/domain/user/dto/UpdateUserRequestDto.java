package com.gavoza.backend.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequestDto {
    private String hometown;
    private String gender;
    private String movedDate;
    private String birthDate;
}
