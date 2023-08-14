package com.gavoza.backend.domain.user.entity;

import com.gavoza.backend.domain.post.entity.Post;
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

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE})
    private List<Post> postList = new ArrayList<>();

//    @JsonManagedReference
//    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE})
//    private List<Post> post;

    public User(String email, String nickname, String password, String hometown, Location location, String movedDate) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.hometown = hometown;
        this.location = location;
        this.movedDate = movedDate;
    }
}
