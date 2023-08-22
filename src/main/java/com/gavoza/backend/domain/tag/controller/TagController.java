package com.gavoza.backend.domain.tag.controller;

import com.gavoza.backend.domain.post.response.PostListResponse;
import com.gavoza.backend.domain.tag.dto.hashtagPostResponseDto;
import com.gavoza.backend.domain.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    //전체 태그 인기순위
    @GetMapping("/All")
    public List<String> allRankNumber(){
        return tagService.allRankNumber();
    }

    //카테고리별 태그 인기순위
    @GetMapping("/category")
    public List<String> categoryRankNumer(@RequestParam String category){
        return tagService.categoryRankNumer(category);
    }

    //전체 인기 순위 태그 post 조회
    @GetMapping("/post/All")
    public PostListResponse hashtagPostResponseDtos(
            @RequestParam int size,
            @RequestParam int page,
            @RequestParam String hashtagName,
            @RequestParam String type
    ){
        return tagService.hashtagPostResponseDtos(size, page-1, hashtagName, type);
    }

    //카테고리별 인기 순위 태그 post 조회
    @GetMapping("/post/category")
    public PostListResponse categoryHashtagPostResponseDtos(
            @RequestParam int size,
            @RequestParam int page,
            @RequestParam String hashtagName,
            @RequestParam String category,
            @RequestParam String type
    ){
        return tagService.categoryHashtagPostResponseDtos(size, page-1, hashtagName, category,type);
    }
}
