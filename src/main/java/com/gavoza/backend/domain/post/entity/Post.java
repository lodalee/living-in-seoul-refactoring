package com.gavoza.backend.domain.post.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gavoza.backend.domain.post.dto.PostRequestDto;
import com.gavoza.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Post extends Auditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

//    @Column(nullable = false)
//    private String nickname;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "location_tag")
    private String locationTag;

    @Column(name = "purpose_tag")
    private String purposeTag;

    @JsonManagedReference
    @OneToMany(mappedBy = "post", cascade = {CascadeType.REMOVE})
    private List<PostImg> postImgList = new ArrayList<>();

    public Post(PostRequestDto requestDto) {
        this.content = requestDto.getContent();
        this.title = requestDto.getTitle();
        this.locationTag = requestDto.getLocationTag();
        this.purposeTag = requestDto.getPurposeTag();
    }
}
