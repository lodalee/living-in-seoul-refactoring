package com.gavoza.backend.domain.tag.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gavoza.backend.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class LocationTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "l_tag_id")
    private Long id;

    @Column(nullable = true)
    private String locationTag;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public LocationTag(String locationTagName, Post post) {
        this.locationTag = locationTagName;
        this.post = post;
    }
}


