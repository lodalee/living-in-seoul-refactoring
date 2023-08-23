package com.gavoza.backend.domain.comment.dto;

import lombok.Getter;

@Getter
public class ReCommentRequestDto {
    private Long commentId;
    private String reComment;
}
