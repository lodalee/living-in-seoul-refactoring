package com.gavoza.backend.domain.comment.dto;

import com.gavoza.backend.domain.comment.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CommentResponseDto {
    private String nickname; // 작성자 이름
    private String comment; // 댓글 내용
    private LocalDateTime createdAt; // 작성 시간
    private String userImg;

    public CommentResponseDto(Comment newComment) {
        this.nickname = newComment.getNickname();
        this.comment = newComment.getComment();
        this.createdAt = newComment.getCreatedAt();
        this.userImg = newComment.getUserImg();
    }
}
