package com.gavoza.backend.domain.comment.dto.request;

import lombok.Getter;

@Getter
public class CommentRequestDto {
    private Long postId;
    private String comment;

}
