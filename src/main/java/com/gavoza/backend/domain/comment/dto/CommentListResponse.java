package com.gavoza.backend.domain.comment.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CommentListResponse {
    private PostPageable pageable;
    private List<CommentResponseDto> comments;


    public CommentListResponse(
            List<CommentResponseDto> comments,

            int totalPages,

            long totalElements,

            int size
    ){
        this.comments = comments;
        this.pageable = new PostPageable(totalPages, totalElements, size);
    }

    @Getter
    private class PostPageable {
        private int totalPages;
        private long totalElements;
        private int size;

        public PostPageable(int totalPages, long totalElements, int size) {
            this.totalPages = totalPages;
            this.totalElements = totalElements;
            this.size = size;
        }
    }
}
