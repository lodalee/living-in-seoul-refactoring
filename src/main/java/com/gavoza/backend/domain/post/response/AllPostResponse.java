package com.gavoza.backend.domain.post.response;

import com.gavoza.backend.domain.post.dto.PostInfoResponseDto;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.user.dto.UserResponseDto;

import java.util.ArrayList;
import java.util.List;

public class AllPostResponse {
    private String msg;
    private PostPageable pageable;
    private List<Result> result = new ArrayList<>();

    public AllPostResponse(String msg, Long totalPages, Long totalElements, Long size,
                    List<Post> posts ) {
        this.msg = msg;
        pageable = new PostPageable(totalPages, totalElements, size);

        for(Post post : posts) {
            PostInfoResponseDto postInfoResponseDto = new PostInfoResponseDto(post);
            UserResponseDto userResponseDto = new UserResponseDto(post.getUser());

            result.add(new Result(userResponseDto, postInfoResponseDto));
        }
    }


//    public AllPostResponse(String msg, Page<Post> posts, Long size) {
//        this.msg = msg;
//        pageable = new PostPageable((long) posts.getTotalPages(), posts.getTotalElements(), size);
//
//        for(Post post : posts) {
//            PostInfoResponseDto postInfoResponseDto = new PostInfoResponseDto(post);
//            UserResponseDto userResponseDto = new UserResponseDto(post.getUser());
//
//            result.add(new Result(userResponseDto, postInfoResponseDto));
//        }
//    }



    private class PostPageable {
        private Long totalPages;
        private Long totalElements;
        private Long size;

        public PostPageable(Long totalPages, Long totalElements, Long size) {
            this.totalPages = totalPages;
            this.totalElements = totalElements;
            this.size = size;
        }
    }

    private class Result {
        UserResponseDto user;
        PostInfoResponseDto post;

        Result(UserResponseDto userResponseDto, PostInfoResponseDto postInfoResponseDto){
            this.user = userResponseDto;
            this.post = postInfoResponseDto;
        }
    }
}
