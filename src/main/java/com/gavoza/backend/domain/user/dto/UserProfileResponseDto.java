package com.gavoza.backend.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDto {
    private String nickname;
    private String birthDate;
    private String movedDate;
    private String gender;
    private String hometown;
    private String profileImageUrl;
    }


