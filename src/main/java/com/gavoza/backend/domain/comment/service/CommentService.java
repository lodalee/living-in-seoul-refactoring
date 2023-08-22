package com.gavoza.backend.domain.comment.service;

import com.gavoza.backend.domain.comment.dto.CommentRequestDto;
import com.gavoza.backend.domain.comment.dto.CommentResponseDto;
import com.gavoza.backend.domain.comment.entity.Comment;
import com.gavoza.backend.domain.comment.repository.CommentRepository;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    //댓글 생성
    public CommentResponseDto createComment(Long id ,CommentRequestDto requestDto, User user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글 입니다."));
        Comment comment = new Comment(requestDto, user.getNickname(), post);
        Comment newComment = commentRepository.save(comment);

        return new CommentResponseDto(newComment);
    }

    @Transactional
    //댓글 수정
    public CommentResponseDto updateComment(Long id, CommentRequestDto requestDto, User user) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        if (!(comment.getNickname().equals(user.getNickname()))){
            throw new IllegalArgumentException("댓글 작성자가 아닙니다.");
        }
        comment.update(requestDto);
        return new CommentResponseDto(comment);
    }

    //댓글 삭제
    public void deleteComment(Long id, User user) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        if (!(comment.getNickname().equals(user.getNickname()))){
            throw new IllegalArgumentException("댓글 작성자가 아닙니다.");
        }
        commentRepository.delete(comment);
    }
}


