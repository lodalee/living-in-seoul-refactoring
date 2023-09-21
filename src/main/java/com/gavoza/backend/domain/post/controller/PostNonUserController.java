package com.gavoza.backend.domain.post.controller;

import com.gavoza.backend.domain.post.dto.response.PostsResponseDto;
import com.gavoza.backend.domain.post.dto.response.PostResponseDto;
import com.gavoza.backend.domain.post.service.PostNonUserService;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostNonUserController {

    private final PostNonUserService postNonUserService;

    //게시글 상세 조회
    @GetMapping("/{postId}")
    public PostResponseDto getOnePost(@PathVariable("postId") Long postId,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails){

        if(Objects.isNull(userDetails)){
            return postNonUserService.getOnePost(postId, null);
        }
        User user = userDetails.getUser();
        return postNonUserService.getOnePost(postId, user);
    }

    //게시글 전체 조회(커뮤티니)
    @GetMapping()
    public PostsResponseDto getPost(@RequestParam int page,
                                    @RequestParam int size,
                                    @AuthenticationPrincipal UserDetailsImpl userDetails){
        if(Objects.isNull(userDetails)){
            return postNonUserService.getPost(page-1 , size, null);
        }
        User user = userDetails.getUser();
        return postNonUserService.getPost(page-1,size, user);
    }

    //태그 인기순위
    @GetMapping("/tag/rank")
    public List<String> rankNumber(@RequestParam String category){
        return postNonUserService.rankNumber(category);
    }

    //태그별 포스트
    @GetMapping("/tag/posts")
    public PostsResponseDto tagsPosts(
            @RequestParam int size,
            @RequestParam int page,
            @RequestParam String hashtagName,
            @RequestParam String type,
            @RequestParam String category,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        if(Objects.isNull(userDetails)){
            return postNonUserService.tagsPosts(size, page-1, hashtagName, type, category, null);
        }
        User user = userDetails.getUser();
        return postNonUserService.tagsPosts(size, page-1, hashtagName, type,category, user);
    }

    //태그별 post + 위치
    @GetMapping("/tag/location")
    public PostsResponseDto postLocation(
            @RequestParam int size,
            @RequestParam int page,
            @RequestParam String gu,
            @RequestParam String category,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        if(Objects.isNull(userDetails)){
            return postNonUserService.postLocation(size, page-1, gu, category, null);
        }
        User user = userDetails.getUser();
        return postNonUserService.postLocation(size, page-1,gu, category, user);
    }
}
