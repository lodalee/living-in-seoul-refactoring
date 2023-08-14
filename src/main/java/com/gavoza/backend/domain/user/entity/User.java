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
    @Column(name = "user")
    private Long Id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String hometown;

    @Column(nullable = false)
    private String movedDate;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String birthDate;

    public User(String email, String nickname, String password, String hometown, String movedDate, String gender, String birthDate) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.hometown = hometown;
        this.movedDate = movedDate;
        this.gender = gender;
        this.birthDate = birthDate;
    }
}
