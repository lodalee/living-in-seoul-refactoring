package com.gavoza.backend.domain.comment.dto;

import com.gavoza.backend.domain.comment.entity.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CommentResponseDto {
    private Long commentId;
    private String comment; // 댓글 내용
    private LocalDateTime createdAt; // 작성 시간
    private int commentLikeSize;
    private List<ReCommentResponseDto> reComments;

    public CommentResponseDto(Comment newComment) {
        this.comment = newComment.getComment();
        this.createdAt = newComment.getCreatedAt();
        this.reComments = newComment.getReCommentList()
                .stream()
                .map(ReCommentResponseDto::new)
                .collect(Collectors.toList());
        this.commentId = newComment.getId();
        this.commentLikeSize = newComment.getCommentLike().size();
    }
}
