package com.gavoza.backend.domain.comment.service;

import com.gavoza.backend.domain.Like.repository.CommentLikeRepository;
import com.gavoza.backend.domain.Like.repository.ReCommentLikeRepository;
import com.gavoza.backend.domain.comment.dto.CommentRequestDto;
import com.gavoza.backend.domain.comment.dto.CommentResponseDto;
import com.gavoza.backend.domain.comment.dto.ReCommentRequestDto;
import com.gavoza.backend.domain.comment.dto.ReCommentResponseDto;
import com.gavoza.backend.domain.comment.entity.Comment;
import com.gavoza.backend.domain.comment.entity.ReComment;
import com.gavoza.backend.domain.comment.repository.CommentRepository;
import com.gavoza.backend.domain.comment.repository.ReCommentRepository;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReCommentRepository reCommentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ReCommentLikeRepository reCommentLikeRepository;

    //comment 조회
    public CommentResponseDto getOneComment(Long id, User user) {
        Comment comment = findComment(id);

        List<ReCommentResponseDto> reComments = comment.getReCommentList().stream()
                .map(reComment -> getOneReComment(reComment.getId(), user))
                .collect(Collectors.toList());

        if(Objects.isNull(user)){
            return new CommentResponseDto(comment, false, reComments);
        }

        boolean hasLikeComment = commentLikeRepository.existsLikeByCommentAndUser(comment, user);
        return new CommentResponseDto(comment, hasLikeComment, reComments);
    }

    @Transactional(readOnly = true)
    public ReCommentResponseDto getOneReComment(Long id, User user) {
        ReComment reComment = reCommentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        boolean reCommentHasLiked = reCommentLikeRepository.existsLikeByReCommentAndUser(reComment, user);

        return new ReCommentResponseDto(reComment, reCommentHasLiked);
    }

    // 댓글 생성
    public CommentResponseDto createComment(Long postId, CommentRequestDto requestDto, User user) {
        Post post = getPostById(postId);
        Comment comment = new Comment(requestDto, user.getNickname(), post);
        Comment newComment = commentRepository.save(comment);

        return new CommentResponseDto(newComment); // ReCommentResponseDto로 변경
    }

    // 대댓글 생성
    public ReCommentResponseDto createReComment(Long commentId, ReCommentRequestDto requestDto, User user) {
        Comment comment = getCommentById(commentId);
        ReComment reComment = new ReComment(requestDto, user.getNickname(), comment);
        ReComment newReComment = reCommentRepository.save(reComment);

        return new ReCommentResponseDto(newReComment);
    }

    // 댓글 수정
    @Transactional
    public CommentResponseDto updateComment(Long commentId, CommentRequestDto requestDto, User user) {
        Comment comment = getCommentById(commentId);
        validateCommentOwnership(comment, user);
        comment.update(requestDto);
        return new CommentResponseDto(comment); // ReCommentResponseDto로 변경
    }

    // 대댓글 수정
    @Transactional
    public ReCommentResponseDto updateReComment(Long reCommentId, ReCommentRequestDto requestDto, User user) {
        ReComment reComment = getReCommentById(reCommentId);
        validateReCommentOwnership(reComment, user);
        reComment.update(requestDto);
        return new ReCommentResponseDto(reComment);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId, User user) {
        Comment comment = getCommentById(commentId);
        validateCommentOwnership(comment, user);
        commentRepository.delete(comment);
    }

    // 대댓글 삭제
    public void deleteReComment(Long reCommentId, User user) {
        ReComment reComment = getReCommentById(reCommentId);
        validateReCommentOwnership(reComment, user);
        reCommentRepository.delete(reComment);
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글 입니다."));
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
    }

    private ReComment getReCommentById(Long reCommentId) {
        return reCommentRepository.findById(reCommentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대댓글입니다."));
    }

    private void validateCommentOwnership(Comment comment, User user) {
        if (!comment.getNickname().equals(user.getNickname())) {
            throw new IllegalArgumentException("댓글 작성자가 아닙니다.");
        }
    }

    private void validateReCommentOwnership(ReComment reComment, User user) {
        if (!reComment.getNickname().equals(user.getNickname())) {
            throw new IllegalArgumentException("댓글 작성자가 아닙니다.");
        }
    }

    //주어진 댓글 ID에 해당하는 게시물 조회
    private Comment findComment(Long id){
        return commentRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 댓글입니다."));
    }

}



