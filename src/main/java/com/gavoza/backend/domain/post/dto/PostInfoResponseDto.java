package com.gavoza.backend.domain.post.dto;

import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.entity.PostImg;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostInfoResponseDto {
    private Long postId;
    private String title;
    private String locationTag;
    private String purposeTag;
    private String content;
    private List<PostImg> postImg;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public PostInfoResponseDto(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.locationTag = post.getLocationTag();
        this.purposeTag = post.getPurposeTag();
        this.content = post.getContent();
        this.postImg = post.getPostImgList();
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
    }
}

