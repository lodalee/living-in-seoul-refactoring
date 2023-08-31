package com.gavoza.backend.domain.scrap.service;

import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.scrap.entity.PostScrap;
import com.gavoza.backend.domain.scrap.repository.PostScrapRepository;
import com.gavoza.backend.domain.user.all.entity.User;
import com.gavoza.backend.domain.user.all.dto.response.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostScrapService {
    private final PostScrapRepository postScrapRepository;
    private final PostRepository postRepository;

    //post 스크랩
    public MessageResponseDto postScrap(Long postId, User user){
        Post post = postRepository.findById(postId).orElseThrow(
                ()-> new IllegalArgumentException("해당 게시글은 존재하지 않습니다.")
        );

        if (!postScrapRepository.existsScrapByPostAndUser(post, user)){
            LocalDateTime scrapedAt = LocalDateTime.now(); // 현재 시간을 스크랩 시간으로 설정
            PostScrap scrap = new PostScrap(post, user, scrapedAt) ;
            postScrapRepository.save(scrap);
            return new MessageResponseDto("스크랩");
        }

        PostScrap scrap = postScrapRepository.findByPostAndUser(post, user).orElseThrow(
                ()-> new IllegalArgumentException("스크랩에 대한 정보가 존재하지 않습니다."));
        postScrapRepository.delete(scrap);
        return new MessageResponseDto("스크랩 취소");
    }
}