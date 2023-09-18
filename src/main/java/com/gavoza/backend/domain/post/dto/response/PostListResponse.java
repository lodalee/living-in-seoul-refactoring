package com.gavoza.backend.domain.post.dto.response;

import com.gavoza.backend.domain.post.dto.response.PostResultDto;
import lombok.Getter;

import java.util.List;

@Getter
public class PostListResponse {
    private String msg;
    private PostPageable pageable;
    private List<PostResultDto> result;

    public PostListResponse(
            String msg,

            int totalPages,

            long totalElements,

            int size,

            List<PostResultDto> postResultDto
    ) {
        this.msg = msg;
        this.pageable = new PostPageable(totalPages, totalElements, size);
        this.result = postResultDto;
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
