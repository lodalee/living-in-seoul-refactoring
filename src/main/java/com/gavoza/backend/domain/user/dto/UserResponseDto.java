package com.gavoza.backend.domain.user.dto;

import com.gavoza.backend.domain.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private String nickname;
    private String email;

    public UserResponseDto(User user) {
        this.nickname = user.getNickname();
        this.email = user.getEmail();
    }
}

