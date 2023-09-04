package com.gavoza.backend.domain.user.all.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
