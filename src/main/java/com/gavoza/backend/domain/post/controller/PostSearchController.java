package com.gavoza.backend.domain.post.controller;

import com.gavoza.backend.domain.post.dto.response.PostsResponseDto;
import com.gavoza.backend.domain.post.service.PostSearchService;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.dto.MessageResponseDto;
import com.gavoza.backend.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class PostSearchController {

    private final PostSearchService postSearchService;

    //게시글 검색
    @GetMapping()
    public PostsResponseDto searchPosts(@RequestParam int page,
                                        @RequestParam int size,
                                        @RequestParam String keyword,
                                        @RequestParam String category,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails){
        if(Objects.isNull(userDetails)){
            return postSearchService.searchPosts(page-1 , size, keyword,category ,null);
        }
        User user = userDetails.getUser();
        return postSearchService.searchPosts(page-1, size, keyword,category, user);
    }

    @PostMapping("/save")
    public ResponseEntity<MessageResponseDto> saveSearch(@RequestParam String query) {
        int firstHashTagIndex = query.indexOf('#');

        //해시태그가 맨 앞에 위치하지 않은 경우, 해시태그가 2개 이상인 경우 에러 메시지를 반환합니다.
        if (firstHashTagIndex != 0 || countHashTags(query) >= 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponseDto("잘못된 검색 요청입니다."));
        }

        if (query.contains("#")) {
            postSearchService.saveSearch(query);
            return ResponseEntity.ok(new MessageResponseDto("Search query saved successfully."));
        }
        return null;
    }

    //오늘 가장 많이 검색된 해시태그
    @GetMapping("/today")
    public List<String> todayRankNumber(){
        return postSearchService.todayRankNumber();
    }

    //query의 # 해시태그의 개수를 세어준다.
    private int countHashTags(String query) {
        int count = 0;
        for (char c : query.toCharArray()) {
            if (c == '#') {
                count++;
            }
        }
        return count;
    }
}

