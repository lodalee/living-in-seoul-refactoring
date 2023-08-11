package com.gavoza.backend.domain.tag.dto;

import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.tag.entity.LocationTag;
import lombok.Getter;

import java.util.stream.Collectors;

@Getter
public class LocationPostResponseDto {
    private String locationTag;
    private String nickname;
    private String content;
//    private String userImg;

    public LocationPostResponseDto(Post post){
        this.locationTag = post.getLocationTag().stream()
                .map(LocationTag::getLocationTag)
                .collect(Collectors.joining());
        this.nickname = post.getUser().getNickname();
        this.content = post.getContent();
//        this.userImg = post.getUser().
    }
}

