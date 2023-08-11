package com.gavoza.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.management.relation.Role;

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

    @Column(nullable = false)
    private String hometown;

    @Column(nullable = false)
    private String movedDate;

    public User(String email, String nickname, String password, String hometown, Location location, String movedDate) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.hometown = hometown;
        this.location = location;
        this.movedDate = movedDate;
    }
}
