package com.gavoza.backend.domain.user.dto;

import com.gavoza.backend.domain.user.entity.User;
import lombok.Getter;

@Getter
public class ProfileResponseDto {
    private String nickname;
    private String email;
    private String gender;
    private String hometown;
    private String movedDate;
    private String birthDate;


    public ProfileResponseDto(User user) {
        this.nickname = user.getNickname();
        this.email = user.getEmail();
    }

    public ProfileResponseDto(String nickname, String email, String gender, String hometown, String movedDate, String birthDate) {
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
        this.hometown = hometown;
        this.movedDate = movedDate;
        this.birthDate = birthDate;
    }
}
