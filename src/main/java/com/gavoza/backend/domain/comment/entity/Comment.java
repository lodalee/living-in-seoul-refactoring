package com.gavoza.backend.domain.comment.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gavoza.backend.domain.Like.entity.Commentlike;
import com.gavoza.backend.domain.comment.dto.CommentRequestDto;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "comments")
@NoArgsConstructor
public class Comment extends Auditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column
    private String nickname;

    @Column(nullable = false, length = 500)
    private  String comment;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @JsonManagedReference
    @OneToMany(mappedBy = "comment", cascade = {CascadeType.REMOVE})
    private List<ReComment> reCommentList = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "comment", cascade = {CascadeType.REMOVE})
    private List<Commentlike> commentLike = new ArrayList<>();

    private String userImg;

    @Transient
    private boolean commentHasLiked;


    public Comment(CommentRequestDto requestDto, String nickname, Post post) {
        this.nickname = nickname;
        this.comment = requestDto.getComment();
        this.post = post;
    }

    public void update(CommentRequestDto requestDto) {
        this.comment = requestDto.getComment();
    }
}
