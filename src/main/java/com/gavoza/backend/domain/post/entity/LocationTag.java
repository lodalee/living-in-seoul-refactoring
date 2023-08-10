package com.gavoza.backend.domain.post.entity;

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

    public LocationTag(String locationTagName) {
        this.locationTag = locationTagName;
    }
}

