package com.gavoza.backend.domain.tag.controller;

import com.gavoza.backend.domain.tag.dto.LocationPostResponseDto;
import com.gavoza.backend.domain.tag.dto.LocationTagResponseDto;
import com.gavoza.backend.domain.tag.dto.PurposePostResponseDto;
import com.gavoza.backend.domain.tag.dto.PurposeTagResponseDto;
import com.gavoza.backend.domain.tag.service.TagService;
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
    public List<PurposeTagResponseDto> purposeTags(){
        return tagService.prankNumber();
    }

    //인기 순위 태그별 post 조회(목적)
    @GetMapping("/purposeTagName")
    public List<PurposePostResponseDto> purposePostResponseDtos(
            @RequestParam int limit,
            @RequestParam String purposeTagName
    ){
        return tagService.purposePostResponseDtos(limit, purposeTagName);
    }
}