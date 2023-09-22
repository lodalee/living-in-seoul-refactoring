package com.gavoza.backend.domain.post.dto.response;

import com.gavoza.backend.domain.user.entity.User;
import lombok.Getter;

@Getter
public class PostUserDto {
    private final String nickname;
    private final String email;
    private final String userImg;

    public PostUserDto(User user) {
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.userImg = user.getProfileImageUrl();
    }

    public PostUserDto(String nickname, String email, String userImg) {
        this.nickname = nickname;
        this.email = email;
        this.userImg = userImg;
    }
}
