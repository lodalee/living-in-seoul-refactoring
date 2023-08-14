package com.gavoza.backend.domain.post.response;

import com.gavoza.backend.domain.post.dto.PostResultDto;
import lombok.Getter;

@Getter
public class PostResponse{
    private String msg;
    private PostResultDto result;
    private boolean hasLiked;

    public PostResponse(String msg, PostResultDto postResultDto, boolean hasLikedPost){
        this.msg = msg;
        this.result = postResultDto;
        this.hasLiked = hasLikedPost;
    }

}
