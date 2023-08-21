package com.gavoza.backend.domain.post.dto;

import lombok.Getter;

@Getter
public class LocationResponseDto {
    private long lat;
    private long lng;

    private String gu;
    private String dong;

    public LocationResponseDto(String gu, String dong, Long lat, Long lng) {
        this.lat = lat;
        this.lng = lng;
        this.gu = gu;
        this.dong = dong;
    }
}
