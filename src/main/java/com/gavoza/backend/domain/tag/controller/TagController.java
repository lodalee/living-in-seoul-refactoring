package com.gavoza.backend.domain.tag.controller;

import com.gavoza.backend.domain.post.response.PostListResponse;
import com.gavoza.backend.domain.tag.service.TagService;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

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
            @RequestParam String type,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        if(Objects.isNull(userDetails)){
            return tagService.hashtagPostResponseDtos(size, page-1, hashtagName, type, null);
        }
        User user = userDetails.getUser();
        return tagService.hashtagPostResponseDtos(size, page-1, hashtagName, type, user);
    }

    //유저 카테고리별 인기 순위 태그 post 조회
    @GetMapping("/post/category")
    public PostListResponse categoryHashtagPostResponseDtos(
            @RequestParam int size,
            @RequestParam int page,
            @RequestParam String hashtagName,
            @RequestParam String category,
            @RequestParam String type,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ){
        if(Objects.isNull(userDetails)){
            return tagService.categoryHashtagPostResponseDtos(size, page-1, hashtagName, category , type, null);
        }
        User user = userDetails.getUser();
        return tagService.categoryHashtagPostResponseDtos(size, page-1, hashtagName, category,type,user);
    }
}
