package com.gavoza.backend.domain.comment.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gavoza.backend.domain.Like.entity.ReCommentLike;
import com.gavoza.backend.domain.comment.dto.request.ReCommentRequestDto;
import com.gavoza.backend.domain.user.entity.User;
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
@Table(name = "re_comments")
@NoArgsConstructor
public class ReComment extends Auditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "re_comment_id")
    private Long id;

    @Column
    private String nickname;

    @Column(nullable = false, length = 500)
    private  String reComment;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    private String userImg;

    @Transient
    private boolean reCommentHasLiked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @OneToMany(mappedBy = "reComment", cascade = {CascadeType.REMOVE})
    private List<ReCommentLike> reCommentLikes = new ArrayList<>();

    public ReComment(ReCommentRequestDto requestDto, String nickname, Comment comment, User user) {
        this.nickname = nickname;
        this.reComment = requestDto.getReComment();
        this.comment = comment;
        this.user = user;
        this.userImg = user.getProfileImageUrl();
    }

    public void update(ReCommentRequestDto requestDto) {
        this.reComment = requestDto.getReComment();
    }
}
