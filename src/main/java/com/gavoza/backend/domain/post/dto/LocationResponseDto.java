package com.gavoza.backend.domain.post.dto;

import lombok.Getter;

@Getter
public class LocationResponseDto {
    private String lname;
    private String address;
    private double lat;
    private double lng;
    private String gu;

    public LocationResponseDto(String lname, String address, double lat, double lng, String gu) {
        this.lname = lname;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.gu = gu;
    }
}
