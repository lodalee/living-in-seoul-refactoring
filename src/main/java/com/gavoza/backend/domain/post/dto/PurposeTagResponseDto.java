package com.gavoza.backend.domain.post.dto;

import lombok.Getter;

@Getter
public class PurposeTagResponseDto {
    private String purposeTag;

    public PurposeTagResponseDto (String purposeTag){
        this.purposeTag = purposeTag;
    }
}
