package com.gavoza.backend.domain.post.dto;

import com.gavoza.backend.domain.user.ToPost.UserResponseDto;
import lombok.Getter;

@Getter
public class PostResultDto {
    UserResponseDto user;
    LocationResponseDto location;
    PostInfoResponseDto post;
    private boolean hasLiked;
    private boolean hasScrapped;


    public PostResultDto(UserResponseDto userResponseDto, PostInfoResponseDto postInfoResponseDto, LocationResponseDto locationResponseDto, boolean hasLiked, boolean hasScrapped) {
        this.user = userResponseDto;
        this.post = postInfoResponseDto;
        this.location = locationResponseDto;
        this.hasLiked = hasLiked;
        this.hasScrapped = hasScrapped;
    }
}