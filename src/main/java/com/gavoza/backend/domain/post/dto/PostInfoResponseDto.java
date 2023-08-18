package com.gavoza.backend.domain.post.dto;

import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.entity.PostImg;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostInfoResponseDto {
    private Long postId;
    private String hashtag;
    private String content;
    private List<PostImg> postImg;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private long lat;
    private long lng;
    private String category;
    private long likeSize;
    private long postViewCount;

    public PostInfoResponseDto(Post post) {
        this.postId = post.getId();
        this.hashtag = post.getHashtag();
        this.content = post.getContent();
        this.postImg = post.getPostImgList();
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
        this.lat = post.getLat();
        this.lng = post.getLng();
        this.category = post.getCategory();
        this.likeSize = post.getLike().size();
        this.postViewCount = post.getPostViewCount();
    }
}

