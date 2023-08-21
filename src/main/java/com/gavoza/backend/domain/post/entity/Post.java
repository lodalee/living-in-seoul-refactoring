package com.gavoza.backend.domain.post.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gavoza.backend.domain.Like.entity.Postlike;
import com.gavoza.backend.domain.post.dto.PostRequestDto;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Post extends Auditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column
    private String hashtag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonManagedReference
    @OneToMany(mappedBy = "post", cascade = {CascadeType.REMOVE})
    private List<PostImg> postImgList = new ArrayList<>();

    private long postViewCount = 0;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.REMOVE})
    private List<Postlike> like = new ArrayList<>();

    private String category;
    private Long lat;
    private Long lng;


    public Post(PostRequestDto requestDto, User user, List<String> uuidFileNames) {
        this.content = requestDto.getContent();
        this.hashtag = requestDto.getHashtag();
        this.user = user;
        this.category = requestDto.getCategory();
        this.lat = requestDto.getLat();
        this.lng = requestDto.getLng();


    }

    public void update(String content, long lat, long lng) {
        this.content = content;
        this.lat = lat;
        this.lng = lng;
    }

    public void increaseViewCount() {
        this.postViewCount += 1;
    }
}
