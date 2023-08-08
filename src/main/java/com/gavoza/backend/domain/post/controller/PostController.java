package com.gavoza.backend.domain.post.controller;

import com.gavoza.backend.domain.post.dto.PostRequestDto;
import com.gavoza.backend.domain.post.dto.PostResponseDto;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.post.service.PostService;
import com.gavoza.backend.global.exception.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;

    //게시글 생성
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDto uploadFile(
            @RequestPart("post") PostRequestDto requestDto,
            @RequestPart("photos") List<MultipartFile> photos) throws IOException {
        return postService.upload(requestDto, photos);
    }

    //게시글 전체 조회(커뮤티니)
    @GetMapping
    public List<PostResponseDto> getPosts() {
        return postService.getPosts();
    }
}
