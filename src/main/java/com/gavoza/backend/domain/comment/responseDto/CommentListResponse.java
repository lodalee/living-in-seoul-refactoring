package com.gavoza.backend.domain.comment.responseDto;

import com.gavoza.backend.domain.comment.dto.CommentResultDto;
import lombok.Getter;

import java.util.List;

@Getter
public class CommentListResponse {
    private String msg;
    private PostPageable pageable;
    private List<CommentResultDto> result;



    public CommentListResponse(
            String msg,

            int totalPages,

            long totalElements,

            int size,

            List<CommentResultDto> commentResultDto
    ){
        this.msg = msg;
        this.pageable = new PostPageable(totalPages, totalElements, size);
        this.result = commentResultDto;
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
