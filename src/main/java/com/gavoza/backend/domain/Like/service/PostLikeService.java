package com.gavoza.backend.domain.Like.service;

import com.gavoza.backend.domain.Like.entity.Postlike;
import com.gavoza.backend.domain.Like.repository.PostLikeRepository;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    public void postLike(Long postId, User user){
        Post post = postRepository.findById(postId).orElseThrow(
                ()-> new IllegalArgumentException("해당 게시글은 존재하지 않습니다.")
        );

        if (!postLikeRepository.existsLikeByPostAndUser(post, user)){
            Postlike like = new Postlike(post, user) ;
            postLikeRepository.save(like);
        } else {
            Postlike like = postLikeRepository.findByPostAndUser(post, user).orElseThrow(
                    ()-> new IllegalArgumentException("좋아요에 대한 정보가 존재하지 않습니다."));
            postLikeRepository.delete(like);
        }
    }
}
