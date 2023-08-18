package com.gavoza.backend.domain.tag.dto;

import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.entity.PostImg;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class hashtagPostResponseDto {
    private String nickname;
    private String userImg;
    private LocalDateTime createdAt;
    private Long postViewCount;
    private String content;
    private String category;
    private String hashTag;
    private List<PostImg> postImg;
    private int likeSize;

    public hashtagPostResponseDto(Post post, String hashTag){
        this.nickname = post.getUser().getNickname();
        this.createdAt = post.getCreatedAt();
        this.postViewCount = post.getPostViewCount();
        this.content = post.getContent();
        this.category = post.getCategory();
        this.hashTag = hashTag;
        this.postImg = post.getPostImgList();
        this.likeSize = post.getLike().size();
//        this.userImg = post.getUser();
    }
}