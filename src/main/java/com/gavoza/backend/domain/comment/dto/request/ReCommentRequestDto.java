package com.gavoza.backend.domain.comment.dto.request;

import lombok.Getter;

@Getter
public class ReCommentRequestDto {
    private Long commentId;
    private String reComment;
}
