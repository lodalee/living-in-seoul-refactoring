package com.gavoza.backend.domain.Like.entity;

import com.gavoza.backend.domain.comment.entity.ReComment;
import com.gavoza.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "re_comment_like")
public class ReCommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "re_comment_like_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "re_comment_id")
    private ReComment reComment;

    public ReCommentLike(ReComment reComment, User user) {
        this.reComment = reComment;
        this.user = user;
    }
    
}
