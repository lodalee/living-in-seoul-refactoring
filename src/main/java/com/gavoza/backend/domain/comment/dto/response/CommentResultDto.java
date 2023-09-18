package com.gavoza.backend.domain.comment.dto.response;

import com.gavoza.backend.domain.post.dto.response.UserResponseDto;
import lombok.Getter;

@Getter
public class CommentResultDto {
    UserResponseDto user;
    CommentResponseDto comment;
    private boolean commentHasLiked;
    private boolean hasReported;

    public CommentResultDto(UserResponseDto userResponseDto, CommentResponseDto commentResponseDto, boolean commentHasLiked, boolean hasReported){
         this.user = userResponseDto;
         this.comment = commentResponseDto;
         this.commentHasLiked = commentHasLiked;
         this.hasReported = hasReported;
    }
}
