package com.gavoza.backend.domain.user.all.entity;

import com.gavoza.backend.domain.Like.entity.Commentlike;
import com.gavoza.backend.domain.Like.entity.Postlike;
import com.gavoza.backend.domain.Like.entity.ReCommentLike;
import com.gavoza.backend.domain.alarm.entity.Alarm;
import com.gavoza.backend.domain.alarm.entity.SubscribeHashtag;
import com.gavoza.backend.domain.comment.entity.Comment;
import com.gavoza.backend.domain.comment.entity.ReComment;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.report.entity.Report;
import com.gavoza.backend.domain.scrap.entity.PostScrap;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user")
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    private Boolean isNew = false;

    private String hometown;

    private String movedDate;

    private String gender;

    private String birthDate;

    private String profileImageUrl;

    private boolean likeAlarm;
    private boolean commentAlarm;
    private boolean hashtagAlarm;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE})
    private List<FavoriteLocation> favoriteLocations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE})
    private List<PostScrap> postScraps = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE})
    private List<Alarm> alarms = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE})
    private List<SubscribeHashtag> subscribeHashtags = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE})
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE})
    private List<ReComment> reComments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE})
    private List<Commentlike> commentLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE})
    private List<Postlike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE})
    private List<ReCommentLike> reCommentLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE})
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE})
    private List<Report> reports = new ArrayList<>();


    public User(String email, String nickname, String password, String hometown, String movedDate, String gender, String birthDate) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.hometown = hometown;
        this.movedDate = movedDate;
        this.gender = gender;
        this.birthDate = birthDate;
        this.isNew = false;
    }

    public User(String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.isNew = false;
    }

    public User(Long id) {
        this.id = id;
    }

    public void changeLikeAlarm() {
        this.likeAlarm = !this.likeAlarm;
    }

    public void changeCommentAlarm() {
        this.commentAlarm = !this.commentAlarm;
    }

    public void changeHashtagAlarm() {
        this.hashtagAlarm = !this.hashtagAlarm;
    }

    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }
}
