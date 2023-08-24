package com.gavoza.backend.domain.comment.dto;

import com.gavoza.backend.domain.comment.entity.Comment;
import com.gavoza.backend.domain.comment.entity.ReComment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CommentResponseDto {
    private String nickname; // 작성자 이름
    private String comment; // 댓글 내용
    private LocalDateTime createdAt; // 작성 시간
    private String userImg;
    private boolean commentHasLiked;
    private List<ReComment> reComments;


    public CommentResponseDto(Comment newComment) {
        this.nickname = newComment.getNickname();
        this.comment = newComment.getComment();
        this.createdAt = newComment.getCreatedAt();
        this.userImg = newComment.getUserImg();
        this.reComments = newComment.getReCommentList();
    }

    public CommentResponseDto(Comment newComment, boolean commentHasLiked){
        this.nickname = newComment.getNickname();
        this.comment = newComment.getComment();
        this.createdAt = newComment.getCreatedAt();
        this.userImg = newComment.getUserImg();
        this.commentHasLiked = commentHasLiked;
        this.reComments = newComment.getReCommentList();
    }
}
