package com.gavoza.backend.domain.tag.dto;

import com.gavoza.backend.domain.post.entity.Post;
import lombok.Getter;

@Getter
public class LocationPostResponseDto {
    private String locationTag;
    private String nickname;
    private String content;
//    private String userImg;

    public LocationPostResponseDto(Post post, String locationTag){
        this.locationTag = locationTag;
        this.nickname = post.getUser().getNickname();
        this.content = post.getContent();
//        this.userImg = post.getUser().
    }

}