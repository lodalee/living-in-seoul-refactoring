package com.gavoza.backend.domain.tag.controller;

import com.gavoza.backend.domain.post.response.PostListResponse;
import com.gavoza.backend.domain.tag.service.TagService;
import com.gavoza.backend.domain.user.all.entity.User;
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

    //태그 인기순위
    @GetMapping("/rank")
    public List<String> rankNumber(@RequestParam String category){
        return tagService.rankNumber(category);
    }

    //태그별 포스트
    @GetMapping("/posts")
    public PostListResponse tagsPosts(
            @RequestParam int size,
            @RequestParam int page,
            @RequestParam String hashtagName,
            @RequestParam String type,
            @RequestParam String category,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        if(Objects.isNull(userDetails)){
            return tagService.tagsPosts(size, page-1, hashtagName, type, category, null);
        }
        User user = userDetails.getUser();
        return tagService.tagsPosts(size, page-1, hashtagName, type,category, user);
    }

    //태그별 post + 위치
    @GetMapping("/posts/location")
    public PostListResponse postLocation(
            @RequestParam int size,
            @RequestParam int page,
            @RequestParam String gu,
            @RequestParam String category,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        if(Objects.isNull(userDetails)){
            return tagService.postLocation(size, page-1, gu, category, null);
        }
        User user = userDetails.getUser();
        return tagService.postLocation(size, page-1,gu, category, user);
    }
}