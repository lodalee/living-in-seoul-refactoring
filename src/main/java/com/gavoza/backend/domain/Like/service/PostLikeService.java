package com.gavoza.backend.domain.Like.service;

import com.gavoza.backend.domain.Like.entity.Commentlike;
import com.gavoza.backend.domain.Like.entity.Postlike;
import com.gavoza.backend.domain.Like.repository.CommentLikeRepository;
import com.gavoza.backend.domain.Like.repository.PostLikeRepository;
import com.gavoza.backend.domain.comment.entity.Comment;
import com.gavoza.backend.domain.comment.repository.CommentRepository;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.exception.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    //post 좋아요
    public MessageResponseDto postLike(Long postId, User user){
        Post post = postRepository.findById(postId).orElseThrow(
                ()-> new IllegalArgumentException("해당 게시글은 존재하지 않습니다.")
        );

        if (!postLikeRepository.existsLikeByPostAndUser(post, user)){
            Postlike like = new Postlike(post, user) ;
            postLikeRepository.save(like);
            return new MessageResponseDto("좋아요");
        }

        Postlike like = postLikeRepository.findByPostAndUser(post, user).orElseThrow(
                ()-> new IllegalArgumentException("좋아요에 대한 정보가 존재하지 않습니다."));
        postLikeRepository.delete(like);
        return new MessageResponseDto("좋아요 취소");
    }

    //comment 좋아요
    public MessageResponseDto commentLike(Long id, User user) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("해당 댓글은 존재하지 않습니다."));

        if (!commentLikeRepository.existsLikeByCommentAndUser(comment, user)){
            Commentlike like = new Commentlike(comment, user);
            commentLikeRepository.save(like);
            return new MessageResponseDto("댓글 좋아요");
        }

        Commentlike like = commentLikeRepository.findByCommentAndUser(comment,user)
                .orElseThrow(
                        ()-> new IllegalArgumentException("좋아요에 대한 정보가 존재하지 않습니다."));
        commentLikeRepository.delete(like);
        return new MessageResponseDto("댓글 좋아요 취소");
    }
}