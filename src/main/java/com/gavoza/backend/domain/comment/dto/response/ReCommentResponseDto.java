package com.gavoza.backend.domain.comment.dto.response;

import com.gavoza.backend.domain.comment.entity.ReComment;
import com.gavoza.backend.domain.post.dto.response.PostUserDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReCommentResponseDto {
    private PostUserDto user;
    private Long reCommentId;
    private String reComment;
    private LocalDateTime createdAt;
    private boolean reCommentHasLiked;
    private int reCommentLikeSize;
    private boolean hasReported;

    public ReCommentResponseDto(ReComment newReComment) {
        this.reComment = newReComment.getReComment();
        this.createdAt = newReComment.getCreatedAt();
        this.reCommentId = newReComment.getId();
        this.user = new PostUserDto(newReComment.getUser());
    }

    public ReCommentResponseDto(ReComment reComment, boolean reCommentHasLiked, boolean hasReported) {
        this.reComment = reComment.getReComment();
        this.createdAt = reComment.getCreatedAt();
        this.reCommentId = reComment.getId();
        this.hasReported = hasReported;
        this.user = new PostUserDto(reComment.getUser());
        this.reCommentHasLiked = reCommentHasLiked;
        this.reCommentLikeSize = reComment.getReCommentLikes().size();
    }
}
