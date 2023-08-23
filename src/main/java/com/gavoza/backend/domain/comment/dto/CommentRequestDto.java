package com.gavoza.backend.domain.comment.dto;

import lombok.Getter;

@Getter
public class CommentRequestDto {
    private Long postId;
    private String comment;

}
