package com.gavoza.backend.domain.tag.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gavoza.backend.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PurposeTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_tag_id")
    private Long id;

    @Column(nullable = true)
    private String purposeTag;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public PurposeTag(String purposeTagName, Post post) {
        this.purposeTag = purposeTagName;
        this.post = post;
    }
}