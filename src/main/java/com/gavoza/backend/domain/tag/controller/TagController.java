package com.gavoza.backend.domain.tag.controller;

import com.gavoza.backend.domain.post.response.PostListResponse;
import com.gavoza.backend.domain.tag.dto.hashtagPostResponseDto;
import com.gavoza.backend.domain.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    //전체 태그 인기순위
    @GetMapping("/All")
    public List<String> hashTags(){
        return tagService.allRankNumber();
    }

    //인기 순위 태그별 post 조회
    @GetMapping("/locationTagName")
    public List<hashtagPostResponseDto> hashtagPostResponseDtos(
            @RequestParam int limit,
            @RequestParam String hashTagName
    ){
        return tagService.hashtagPostResponseDtos(limit, hashTagName);
    }

    //게시글 전체 조회
    @GetMapping("/locationTagsAll")
    @ResponseStatus(HttpStatus.OK)
    public PostListResponse gethashtagPostAll(@RequestParam int page,
                                               @RequestParam int size,
                                               @RequestParam String hashTagName){
        return tagService.gethashtagPost(page-1,size, hashTagName);
    }
}