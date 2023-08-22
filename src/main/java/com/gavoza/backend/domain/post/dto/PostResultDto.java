package com.gavoza.backend.domain.post.dto;

import com.gavoza.backend.domain.user.ToPost.UserResponseDto;
import lombok.Getter;

@Getter
public class PostResultDto {
    UserResponseDto user;
    PostInfoResponseDto post;
    LocationResponseDto location;
    private boolean hasLiked;


    public PostResultDto(UserResponseDto userResponseDto, PostInfoResponseDto postInfoResponseDto, LocationResponseDto locationResponseDto, boolean hasLiked) {
        this.user = userResponseDto;
        this.post = postInfoResponseDto;
        this.location = locationResponseDto;
        this.hasLiked = hasLiked;
    }
}