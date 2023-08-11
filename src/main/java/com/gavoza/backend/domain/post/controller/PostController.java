package com.gavoza.backend.domain.post.controller;

import com.gavoza.backend.domain.post.dto.PostRequestDto;
import com.gavoza.backend.domain.post.response.AllPostResponse;
import com.gavoza.backend.domain.post.service.PostService;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.exception.MessageResponseDto;
import com.gavoza.backend.global.security.UserDetailsImpl;
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
//    private final PostRepository postRepository;

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

//    //게시글 전체 조회(커뮤티니)
//    @GetMapping
//    public AllPostResponse getPosts() {
//        return postService.getPosts();
//    }
}
