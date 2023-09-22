package com.gavoza.backend.domain.post.dto.response;

import lombok.Getter;

@Getter
public class PostResponseDto {
    private String msg;
    private PostResultDto result;
    //경도, 위도 추가

    public PostResponseDto(String msg, PostResultDto postResultDto){
        this.msg = msg;
        this.result = postResultDto;
    }
}