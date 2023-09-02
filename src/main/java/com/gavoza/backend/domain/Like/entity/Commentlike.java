package com.gavoza.backend.domain.Like.entity;

import com.gavoza.backend.domain.comment.entity.Comment;
import com.gavoza.backend.domain.user.all.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "comment_like")
public class Commentlike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "comment_id")
    private Comment comment;


    public Commentlike(Comment comment, User user) {
        this.comment = comment;
        this.user = user;
    }
}
