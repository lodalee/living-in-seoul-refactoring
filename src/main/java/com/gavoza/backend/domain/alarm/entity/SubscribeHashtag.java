package com.gavoza.backend.domain.alarm.entity;

import com.gavoza.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "sub_hashtag")
@NoArgsConstructor
public class SubscribeHashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_hashtag_id")
    private Long Id;

    @Column(nullable = false)
    private String hashtag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public SubscribeHashtag(User user, String hashtag) {
        this.user = user;
        this.hashtag = hashtag;
    }
}
