package com.gavoza.backend.domain.user.entity;

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
    @Column(name = "user_id")
    private Long Id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    private String hometown;

    private String movedDate;

    private String gender;


    public User(String email, String nickname, String encodedPassword, String hometown, Location guDong, String movedDate, String gender) {
        this.email = email;
        this.nickname = nickname;
        this.password = encodedPassword;
        this.location = guDong;
        this.hometown = hometown;
        this.movedDate = movedDate;
        this.gender = gender;
    }
}
