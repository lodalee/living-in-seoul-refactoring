package com.gavoza.backend.domain.post.controller;

import com.gavoza.backend.domain.post.dto.request.PostRequestDto;
import com.gavoza.backend.domain.post.dto.response.PostsResponseDto;
import com.gavoza.backend.domain.post.service.PostUserService;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.dto.MessageResponseDto;
import com.gavoza.backend.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users_post")
public class PostUserController {

    private final PostUserService postUserService;

    //게시글 생성
    @PostMapping
    public MessageResponseDto uploadFile(
            @RequestPart("post") PostRequestDto requestDto,
            @RequestPart("photos") List<MultipartFile> photos,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        User user = userDetails.getUser();
        return postUserService.upload(requestDto,user,photos);
    }

    //게시글 수정
    @PutMapping("/{postId}")
    public MessageResponseDto updatePost(@PathVariable("postId") Long postId,
                                         @RequestBody PostRequestDto requestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        postUserService.updatePost(postId, requestDto, user);

        return new MessageResponseDto("게시글 수정 성공");
    }

    //게시글 삭제
    @DeleteMapping("/{postId}")
    public MessageResponseDto deletePost(@PathVariable("postId") Long postId,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();

        postUserService.deletePost(postId, user);

        return new MessageResponseDto("게시글 삭제 성공");
    }

    //내가 쓴 글 조회
    @GetMapping("/my")
    public PostsResponseDto getMyPost(@RequestParam int page,
                                      @RequestParam int size,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return postUserService.getMyPost(page-1, size, user);
    }

    //포스트 좋아요
    @PostMapping("/like/{postId}")
    public MessageResponseDto postLike(@PathVariable Long postId,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return postUserService.postLike(postId,user);
    }
}
