package com.gavoza.backend.domain.scrap.service;

import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.scrap.entity.PostScrap;
import com.gavoza.backend.domain.scrap.repository.PostScrapRepository;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.exception.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        if (!postScrapRepository.existsLikeByPostAndUser(post, user)){
            PostScrap scrap = new PostScrap(post, user) ;
            postScrapRepository.save(scrap);
            return new MessageResponseDto("스크랩");
        }

        PostScrap scrap = postScrapRepository.findByPostAndUser(post, user).orElseThrow(
                ()-> new IllegalArgumentException("좋아요에 대한 정보가 존재하지 않습니다."));
        postScrapRepository.delete(scrap);
        return new MessageResponseDto("스크랩 취소");
    }
}