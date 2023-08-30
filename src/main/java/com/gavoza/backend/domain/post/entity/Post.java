package com.gavoza.backend.domain.post.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gavoza.backend.domain.Like.entity.Postlike;
import com.gavoza.backend.domain.comment.entity.Comment;
import com.gavoza.backend.domain.post.dto.PostRequestDto;
import com.gavoza.backend.domain.report.entity.Report;
import com.gavoza.backend.domain.scrap.entity.PostScrap;
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

    @JsonManagedReference
    @OneToMany(mappedBy = "post", cascade = {CascadeType.REMOVE})
    private List<Postlike> like = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "post", cascade = {CascadeType.REMOVE})
    private List<PostScrap> scraps = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "post", cascade = {CascadeType.REMOVE})
    private List<Report> reports = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "post", cascade = {CascadeType.REMOVE})
    private List<Comment> commentList = new ArrayList<>();

    private String category;
    private double lat;
    private double lng;
    private String gu;
    private String lname;
    private String address;


    public Post(PostRequestDto requestDto, User user) {
        this.content = requestDto.getContent();
        this.hashtag = requestDto.getHashtag();
        this.user = user;
        this.category = requestDto.getCategory();
        this.lat = requestDto.getLat();
        this.lng = requestDto.getLng();
        this.gu = requestDto.getGu();
        this.lname = requestDto.getLname();
        this.address = requestDto.getAddress();
    }

    public void update(String content, double lat, double lng) {
        this.content = content;
        this.lat = lat;
        this.lng = lng;
    }

    public void increaseViewCount() {
        this.postViewCount += 1;
    }

    public List<Comment> getComments() {
        return commentList;
    }
}

