package com.gavoza.backend.domain.post.entity;

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

    public PurposeTag(String purposeTagName) {
        this.purposeTag = purposeTagName;
    }
}