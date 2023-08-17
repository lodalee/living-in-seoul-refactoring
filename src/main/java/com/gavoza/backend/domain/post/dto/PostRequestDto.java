package com.gavoza.backend.domain.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostRequestDto {
    private String title;
    private String locationTag;
    private String purposeTag;
    private String content;
    private String postImg;
    private String categories;

    public void validateCategory() {
        List<String> validCategories = Arrays.asList("생활정보", "후기", "서울시정책", "전체");

        if (!validCategories.contains(this.categories)) {
            throw new IllegalArgumentException("존재하지 않는 카테고리 입니다.");
        }
    }
}
