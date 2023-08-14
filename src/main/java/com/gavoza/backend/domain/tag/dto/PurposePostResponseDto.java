package com.gavoza.backend.domain.tag.dto;

import com.gavoza.backend.domain.post.entity.Post;
import lombok.Getter;

@Getter
public class PurposePostResponseDto {
    private String purposeTag;
    private String nickname;
    private String content;

    public PurposePostResponseDto(Post post, String purposeTag) {
        this.purposeTag = purposeTag;
        this.nickname = post.getUser().getNickname();
        this.content = post.getContent();
    }
}

