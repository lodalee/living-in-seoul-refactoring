package com.gavoza.backend.domain.scrap.entity;

import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.user.all.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post_scrap")
public class PostScrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_scrap_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "post_id")
    private Post post;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime scrapedAt; // 스크랩한 시간을 저장할 필드

    public PostScrap(Post post, User user, LocalDateTime scrapedAt) {
        this.post = post;
        this.user = user;
        this.scrapedAt = scrapedAt;
    }
}
