package com.gavoza.backend.domain.comment.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gavoza.backend.domain.comment.dto.ReCommentRequestDto;
import com.gavoza.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public ReComment(ReCommentRequestDto requestDto, String nickname, Comment comment) {
        this.nickname = nickname;
        this.reComment = requestDto.getReComment();
        this.comment = comment;
    }

    public void update(ReCommentRequestDto requestDto) {
        this.reComment = requestDto.getReComment();
    }
}
