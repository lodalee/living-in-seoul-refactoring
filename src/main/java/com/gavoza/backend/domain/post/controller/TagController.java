package com.gavoza.backend.domain.post.controller;

import com.gavoza.backend.domain.post.dto.LocationPostResponseDto;
import com.gavoza.backend.domain.post.dto.LocationTagResponseDto;
import com.gavoza.backend.domain.post.dto.PurposeTagResponseDto;
import com.gavoza.backend.domain.post.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    //인기 순위 태그 조회(위치)
    @GetMapping("/locationTags")
    public List<LocationTagResponseDto> locationTags(){
        return tagService.rankNumber();
    }

    //인기 순위 태그별 post 조회(위치)
    @GetMapping("/locationTagName")
    public List<LocationPostResponseDto> locationPostResponseDtos(
            @RequestParam int limit,
            @RequestParam String locationTagName
            ){
        return tagService.locationPostResponseDtos(limit, locationTagName);
    }

    //인기 순위 태그 조회(목적)
    @GetMapping("/purposeTags")
    public List<PurposeTagResponseDto> pulposeTags(){
        return tagService.prankNumber();
    }
}
