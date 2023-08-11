package com.gavoza.backend.domain.tag.dto;

import lombok.Getter;

@Getter
public class PurposeTagResponseDto {
    private String purposeTag;

    public PurposeTagResponseDto (String purposeTag){
        this.purposeTag = purposeTag;
    }
}

