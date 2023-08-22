package com.gavoza.backend.domain.user.ToPost;

import com.gavoza.backend.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private final String nickname;
    private final String email;

    public UserResponseDto(User user) {
        this.nickname = user.getNickname();
        this.email = user.getEmail();
    }

    public UserResponseDto(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }
}
