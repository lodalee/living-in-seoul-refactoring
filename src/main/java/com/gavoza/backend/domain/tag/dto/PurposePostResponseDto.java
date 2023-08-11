package com.gavoza.backend.domain.tag.dto;

import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.tag.entity.PurposeTag;
import lombok.Getter;

import java.util.stream.Collectors;

@Getter
public class PurposePostResponseDto {
    private String purposeTag;
    private String nickname;
    private String content;
//    private String userImg;

    public PurposePostResponseDto(Post post){
        this.purposeTag = post.getPurposeTag().stream()
                .map(PurposeTag::getPurposeTag)
                .collect(Collectors.joining());
        this.nickname = post.getUser().getNickname();
        this.content = post.getContent();
//        this.userImg = post.getUser().
    }
}

