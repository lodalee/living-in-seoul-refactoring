package com.gavoza.backend.domain.comment.dto;

import com.gavoza.backend.domain.comment.entity.Comment;
import com.gavoza.backend.domain.comment.entity.ReComment;

import java.time.LocalDateTime;

public class ReCommentResponseDto {
    private String nickname;
    private String reComment;
    private LocalDateTime createdAt;
    private String userImg;

    public ReCommentResponseDto(ReComment newReComment) {
        this.nickname = newReComment.getNickname();
        this.reComment = newReComment.getReComment();
        this.createdAt = newReComment.getCreatedAt();
        this.userImg = newReComment.getUserImg();
    }
}
