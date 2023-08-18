package com.gavoza.backend.domain.Like.controller;

import com.gavoza.backend.domain.Like.service.PostLikeService;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.exception.MessageResponseDto;
import com.gavoza.backend.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostLikeController {
    private final PostLikeService postLikeService;

    @PostMapping("/posts/{postId}/like")
    public MessageResponseDto postLike(@PathVariable Long postId,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        postLikeService.postLike(postId,user);

        return new MessageResponseDto("좋아요");
    }
}
