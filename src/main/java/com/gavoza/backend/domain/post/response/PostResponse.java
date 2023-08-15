package com.gavoza.backend.domain.post.response;

import com.gavoza.backend.domain.post.dto.PostResultDto;
import com.gavoza.backend.domain.post.entity.Post;
import lombok.Getter;

@Getter
public class PostResponse{
    private String msg;
    private PostResultDto result;
    private boolean hasLiked;
    private long postViewCount;

    public PostResponse(Post post, String msg, PostResultDto postResultDto, boolean hasLikedPost){
        this.msg = msg;
        this.result = postResultDto;
        this.hasLiked = hasLikedPost;
        this.postViewCount = post.getPostViewCount();
    }

}
