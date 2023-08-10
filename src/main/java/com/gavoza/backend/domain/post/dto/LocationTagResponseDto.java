package com.gavoza.backend.domain.post.dto;

import lombok.Getter;

@Getter
public class LocationTagResponseDto {
    private String locationTag;

    public LocationTagResponseDto(String locationTag){
        this.locationTag = locationTag;
    }
}
