package com.gavoza.backend.domain.post.controller;

import com.gavoza.backend.domain.post.dto.response.PostsResponseDto;
import com.gavoza.backend.domain.post.service.PostScrapService;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.dto.MessageResponseDto;
import com.gavoza.backend.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scrap")
public class PostScrapController {
    private final PostScrapService postScrapService;

    //포스트 스크랩
    @PostMapping("/{postId}")
    public MessageResponseDto postScrap(@PathVariable Long postId,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return postScrapService.postScrap(postId,user);
    }

    //내가 스크랩한 글 조회
    @GetMapping("/my")
    public PostsResponseDto getMyScrap(@RequestParam int page,
                                       @RequestParam int size,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return postScrapService.getMyScrap(page-1, size, user);
    }
}
