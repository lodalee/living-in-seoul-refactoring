package com.gavoza.backend.domain.post.dto;

import lombok.Getter;

@Getter
public class LocationResponseDto {
    private double lat;
    private double lng;
    private String gu;
    private String dong;

    public LocationResponseDto(String gu, String dong, double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
        this.gu = gu;
        this.dong = dong;
    }
}
