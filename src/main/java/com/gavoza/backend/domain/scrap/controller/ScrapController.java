package com.gavoza.backend.domain.scrap.controller;

import com.gavoza.backend.domain.scrap.service.PostScrapService;
import com.gavoza.backend.domain.user.all.entity.User;
import com.gavoza.backend.domain.user.all.dto.response.MessageResponseDto;
import com.gavoza.backend.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ScrapController {
    private final PostScrapService postScrapService;

    //포스트 스크랩
    @PostMapping("/posts/{postId}/scrap")
    public MessageResponseDto postScrap(@PathVariable Long postId,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return postScrapService.postScrap(postId,user);
    }
}
