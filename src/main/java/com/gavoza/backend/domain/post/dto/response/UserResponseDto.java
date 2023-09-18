package com.gavoza.backend.domain.post.dto.response;

import com.gavoza.backend.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private final String nickname;
    private final String email;
    private final String userImg;

    public UserResponseDto(User user) {
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.userImg = user.getProfileImageUrl();
    }

    public UserResponseDto(String nickname, String email, String userImg) {
        this.nickname = nickname;
        this.email = email;
        this.userImg = userImg;
    }
}
