package com.gavoza.backend.domain.post.dto;

import com.gavoza.backend.domain.user.dto.UserResponseDto;
import lombok.Getter;

@Getter
public class PostResultDto {
    UserResponseDto user;
    PostInfoResponseDto post;
    LocationResponseDto location;

    public PostResultDto(UserResponseDto userResponseDto, PostInfoResponseDto postInfoResponseDto, LocationResponseDto locationResponseDto) {
        this.user = userResponseDto;
        this.post = postInfoResponseDto;
        this.location = locationResponseDto;
    }
}