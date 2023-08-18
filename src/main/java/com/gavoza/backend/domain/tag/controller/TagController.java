package com.gavoza.backend.domain.tag.controller;

import com.gavoza.backend.domain.tag.dto.hashtagPostResponseDto;
import com.gavoza.backend.domain.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    //전체 태그 인기순위
    @GetMapping("/All")
    public List<String> allRankNumber(){
        return tagService.allRankNumber();
    }

    //카테고리별 태그 인기순위
    @GetMapping("/category")
    public List<String> categoryRankNumer(@RequestParam String category){
        return tagService.categoryRankNumer(category);
    }

    //전체 인기 순위 태그 post 조회
    @GetMapping("/post/All")
    public List<hashtagPostResponseDto> hashtagPostResponseDtos(
            @RequestParam int limit,
            @RequestParam String hashtagName
    ){
        return tagService.hashtagPostResponseDtos(limit, hashtagName);
    }

    //카테고리별 인기 순위 태그 post 조회
    @GetMapping("/post/category")
    public List<hashtagPostResponseDto> categoryHashtagPostResponseDtos(
            @RequestParam int limit,
            @RequestParam String hashtagName,
            @RequestParam String category,
            @RequestParam String type
    ){
        return tagService.categoryHashtagPostResponseDtos(limit, hashtagName, category,type);
    }
//
//    //게시글 전체 조회
//    @GetMapping("/locationTagsAll")
//    @ResponseStatus(HttpStatus.OK)
//    public PostListResponse gethashtagPostAll(@RequestParam int page,
//                                               @RequestParam int size,
//                                               @RequestParam String hashTagName){
//        return tagService.gethashtagPost(page-1,size, hashTagName);
//    }


}
