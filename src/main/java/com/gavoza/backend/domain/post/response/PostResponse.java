package com.gavoza.backend.domain.post.response;

import com.gavoza.backend.domain.post.dto.PostResultDto;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PostResponse{
    private String msg;
    private PostResultDto result;
    //경도, 위도 추가

    public PostResponse(String msg, PostResultDto postResultDto){
        this.msg = msg;
        this.result = postResultDto;
    }
}