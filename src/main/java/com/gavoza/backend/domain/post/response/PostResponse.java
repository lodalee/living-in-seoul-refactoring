package com.gavoza.backend.domain.post.response;

import com.gavoza.backend.domain.post.dto.PostInfoResponseDto;
import com.gavoza.backend.domain.user.dto.UserResponseDto;
import lombok.Getter;

@Getter
public class PostResponse {
    private String msg;
    private Result result;

    PostResponse(String msg, UserResponseDto userResponseDto, PostInfoResponseDto postInfoResponseDto){
        this.msg = msg;
        this.result = new Result(userResponseDto, postInfoResponseDto);
    }

    private class Result {
        UserResponseDto user;
        PostInfoResponseDto post;

        Result(UserResponseDto userResponseDto, PostInfoResponseDto postInfoResponseDto){
            this.user = userResponseDto;
            this.post = postInfoResponseDto;
        }
    }
}
