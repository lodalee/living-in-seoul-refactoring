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
    private String nickname; // 작성자 이름
    private String comment; // 댓글 내용
    private String userImg;
    private int commentSize;
    private LocalDateTime createdAt; // 작성 시간
    private boolean commentHasLiked;
    private List<ReCommentResponseDto> reComments;

    public CommentResponseDto(Comment newComment) {
        this.nickname = newComment.getNickname();
        this.comment = newComment.getComment();
        this.createdAt = newComment.getCreatedAt();
        this.userImg = newComment.getUserImg();
        this.reComments = newComment.getReCommentList()
                .stream()
                .map(ReCommentResponseDto::new)
                .collect(Collectors.toList());
        this.commentId = newComment.getId();
        this.commentSize = newComment.getReCommentList().size();
    }

    public CommentResponseDto(Comment comment, boolean hasLikeComment, List<ReCommentResponseDto> reComments) {
        this.nickname = comment.getNickname();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt();
        this.userImg = comment.getUserImg();
        this.commentHasLiked = hasLikeComment;
        this.reComments = reComments;
        this.commentId = comment.getId();
        this.commentSize = comment.getReCommentList().size();
    }
}
