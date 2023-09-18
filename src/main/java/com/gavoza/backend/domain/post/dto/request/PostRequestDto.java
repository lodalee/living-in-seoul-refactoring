package com.gavoza.backend.domain.post.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostRequestDto {
    private String hashtag;
    private String content;
    private String postImg;
    private String category;
    private double lat;
    private double lng;
    private String gu;
    private String lname;
    private String address;

    public void validateCategory() {
        List<String> validCategories = Arrays.asList("생활정보", "후기", "동향소통");

        if (!validCategories.contains(this.category)) {
            throw new IllegalArgumentException("존재하지 않는 카테고리 입니다.");
        }
    }
}
