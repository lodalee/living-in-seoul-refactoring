package com.gavoza.backend.domain.post.dto;

import lombok.Getter;

@Getter
public class PostResultDto {
//    private UserResponseDto userResponseDto;
    private PostInfoResponseDto postInfoResponseDto;

    public PostResultDto(PostInfoResponseDto postInfoResponseDto) {
        this.postInfoResponseDto = postInfoResponseDto;
    }
}
