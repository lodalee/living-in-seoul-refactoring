package com.gavoza.backend.domain.comment.dto.response;

import com.gavoza.backend.domain.post.dto.response.PostUserDto;
import lombok.Getter;

@Getter
public class CommentResultDto {
    PostUserDto user;
    CommentResponseDto comment;
    private boolean commentHasLiked;
    private boolean hasReported;

    public CommentResultDto(PostUserDto postUserDto, CommentResponseDto commentResponseDto, boolean commentHasLiked, boolean hasReported){
         this.user = postUserDto;
         this.comment = commentResponseDto;
         this.commentHasLiked = commentHasLiked;
         this.hasReported = hasReported;
    }
}
