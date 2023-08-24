package com.gavoza.backend.domain.post.dto;

import com.gavoza.backend.domain.comment.dto.CommentResponseDto;
import com.gavoza.backend.domain.user.ToPost.UserResponseDto;
import lombok.Getter;

import java.util.List;

@Getter
public class PostResultDto {
    UserResponseDto user;
    PostInfoResponseDto post;
    LocationResponseDto location;
    private boolean hasLiked;
    List<CommentResponseDto> comments;


    public PostResultDto(UserResponseDto userResponseDto, PostInfoResponseDto postInfoResponseDto, LocationResponseDto locationResponseDto, boolean hasLiked) {
        this.user = userResponseDto;
        this.post = postInfoResponseDto;
        this.location = locationResponseDto;
        this.hasLiked = hasLiked;
    }

    public PostResultDto(UserResponseDto userResponseDto, PostInfoResponseDto postInfoResponseDto, LocationResponseDto locationResponseDto, boolean hasLikedPost, List<CommentResponseDto> commentResponseDtos) {
        this.user = userResponseDto;
        this.post = postInfoResponseDto;
        this.location = locationResponseDto;
        this.hasLiked = hasLikedPost;
        this.comments = commentResponseDtos;
    }
}