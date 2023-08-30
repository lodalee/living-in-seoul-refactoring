package com.gavoza.backend.domain.comment.dto;

import com.gavoza.backend.domain.comment.entity.ReComment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReCommentResponseDto {
    private Long reCommentId;
    private String nickname;
    private String reComment;
    private String userImg;
    private LocalDateTime createdAt;
    private boolean reCommentHasLiked;
    private int reCommentLikeSize;
    private boolean hasReported;


    public ReCommentResponseDto(ReComment newReComment) {
        this.nickname = newReComment.getNickname();
        this.reComment = newReComment.getReComment();
        this.createdAt = newReComment.getCreatedAt();
        this.userImg = newReComment.getUserImg();
        this.reCommentId = newReComment.getId();
        this.reCommentLikeSize = newReComment.getReCommentLikes().size();
    }

    public ReCommentResponseDto(ReComment reComment, boolean reCommentHasLiked, boolean hasReported) {
        this.nickname = reComment.getNickname();
        this.reComment = reComment.getReComment();
        this.createdAt = reComment.getCreatedAt();
        this.userImg = reComment.getUserImg();
        this.reCommentHasLiked = reCommentHasLiked;
        this.reCommentId = reComment.getId();
        this.reCommentLikeSize = reComment.getReCommentLikes().size();
        this.hasReported = hasReported;
    }
}
