package com.gavoza.backend.domain.post.controller;

import com.gavoza.backend.domain.post.dto.PostRequestDto;
import com.gavoza.backend.domain.post.response.PostListResponse;
import com.gavoza.backend.domain.post.response.PostResponse;
import com.gavoza.backend.domain.post.service.PostService;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.exception.MessageResponseDto;
import com.gavoza.backend.global.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    //게시글 생성
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDto uploadFile(
            @RequestPart("post") PostRequestDto requestDto,
            @RequestPart("photos") List<MultipartFile> photos,
            @AuthenticationPrincipal UserDetailsImpl userDetails
          ) throws IOException {
        User user = userDetails.getUser();
        return postService.upload(requestDto, photos, user);
    }

    //게시글 수정
    @PutMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDto updatePost(@PathVariable("postId") Long postId,
                                         @RequestBody PostRequestDto requestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        postService.updatePost(postId, requestDto, user);

        return new MessageResponseDto("게시글 수정 성공");
    }

    //게시글 삭제
    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDto deletePost(@PathVariable("postId") Long postId,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();

        postService.deletePost(postId, user);

        return new MessageResponseDto("게시글 삭제 성공");
    }

    //게시글 상세 조회
    @GetMapping("/get/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse getOnePost(@PathVariable("postId") Long postId,
                                   HttpServletRequest request){
        User user = (User) request.getAttribute("user");
        return postService.getOnePost(postId, user);
    }

    //게시글 전체 조회(커뮤티니)
    @GetMapping("/get")
    @ResponseStatus(HttpStatus.OK)
    public PostListResponse getPost(@RequestParam int page,
                                    @RequestParam int size){
        return postService.getPost(page-1,size);
    }
}
