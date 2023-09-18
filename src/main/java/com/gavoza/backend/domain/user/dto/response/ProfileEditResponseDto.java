package com.gavoza.backend.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEditResponseDto {
    private String nickname;
    private String birthDate;
    private String movedDate;
    private String gender;
    private String hometown;
    private String profileImageUrl;
}


