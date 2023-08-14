package com.gavoza.backend.domain.post.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @Column(name = "title", nullable = false)
    private String title;

    @Column
    private String locationTag;

    @Column
    private String purposeTag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonManagedReference
    @OneToMany(mappedBy = "post", cascade = {CascadeType.REMOVE})
    private List<PostImg> postImgList = new ArrayList<>();

    private long postViewCount = 0;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.REMOVE})
    private List<Postlike> like = new ArrayList<>();


    public Post(PostRequestDto requestDto, User user) {
        this.content = requestDto.getContent();
        this.title = requestDto.getTitle();
        this.locationTag = requestDto.getLocationTag();
        this.purposeTag = requestDto.getPurposeTag();
        this.user = user;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void increaseViewCount() {
        this.postViewCount += 1;
    }
}
