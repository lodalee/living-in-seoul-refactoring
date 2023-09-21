package com.gavoza.backend.domain.post.dto.response;

import lombok.Getter;

@Getter
public class PostResultDto {
    PostUserDto user;
    LocationResponseDto location;
    PostInfoResponseDto post;
    private boolean hasLiked;
    private boolean hasScrapped;
    private boolean hasReported;



    public PostResultDto(PostUserDto postUserDto, PostInfoResponseDto postInfoResponseDto, LocationResponseDto locationResponseDto, boolean hasLiked, boolean hasScrapped, boolean hasReported) {
        this.user = postUserDto;
        this.post = postInfoResponseDto;
        this.location = locationResponseDto;
        this.hasLiked = hasLiked;
        this.hasScrapped = hasScrapped;
        this.hasReported = hasReported;
    }
}