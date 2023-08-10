package com.gavoza.backend.domain.post.dto;

import com.gavoza.backend.domain.post.entity.Post;
import lombok.Getter;

@Getter
public class LocationPostResponseDto {
    private String locationTag;
    private String nickname;
    private String content;
//    private String userImg;

    public LocationPostResponseDto(Post post){
        this.locationTag = post.getLocationTag().getLocationTag();
        this.nickname = post.getUser().getNickname();
        this.content = post.getContent();
//        this.userImg = post.getUser().
    }
}
