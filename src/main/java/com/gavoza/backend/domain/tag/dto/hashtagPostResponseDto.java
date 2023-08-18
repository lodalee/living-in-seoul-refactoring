package com.gavoza.backend.domain.tag.dto;

import com.gavoza.backend.domain.post.entity.Post;
import lombok.Getter;

@Getter
public class hashtagPostResponseDto {
    private String hashTag;
    private String nickname;
    private String content;
    private String userImg;

    public hashtagPostResponseDto(Post post, String hashTag){
        this.hashTag = hashTag;
        this.nickname = post.getUser().getNickname();
        this.content = post.getContent();
//        this.userImg = post.getUser();
    }
}