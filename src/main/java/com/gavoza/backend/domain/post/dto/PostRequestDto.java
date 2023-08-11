package com.gavoza.backend.domain.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRequestDto {
    private String title;
    private String locationTag;
    private String purposeTag;
    private String content;
    private String postImg;
}
