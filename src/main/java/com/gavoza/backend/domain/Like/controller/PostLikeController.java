package com.gavoza.backend.domain.Like.controller;

import com.gavoza.backend.domain.Like.service.PostLikeService;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.dto.MessageResponseDto;
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

    //포스트 좋아요
    @PostMapping("/posts/{postId}/like")
    public MessageResponseDto postLike(@PathVariable Long postId,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return postLikeService.postLike(postId,user);
    }

    //댓글 좋아요
    @PostMapping("/comment/{id}/like") //comment id
    public MessageResponseDto commentLike(@PathVariable Long id,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return postLikeService.commentLike(id, user);
    }

    //대댓글 좋아요
    @PostMapping("/recomment/{id}/like") //recomment id
    public MessageResponseDto recommentLike(@PathVariable Long id,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return postLikeService.recommentLike(id,user);
    }
}
