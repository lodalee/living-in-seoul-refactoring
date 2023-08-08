package com.gavoza.backend.domain.post.dto;

import com.gavoza.backend.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostResponseDto {
    private String msg;
    private List<PostResultDto> result;

    public PostResponseDto(Post post, String msg) {
        this.msg = msg;
        this.result = Arrays.asList(new PostResultDto(new PostInfoResponseDto(post)));
    }
}
