package com.gavoza.backend.domain.comment.service;

import com.gavoza.backend.domain.Like.repository.CommentLikeRepository;
import com.gavoza.backend.domain.Like.repository.ReCommentLikeRepository;
import com.gavoza.backend.domain.alarm.AlarmEventType;
import com.gavoza.backend.domain.alarm.entity.Alarm;
import com.gavoza.backend.domain.alarm.repository.AlarmRepository;
import com.gavoza.backend.domain.alarm.sse.NotificationService;
import com.gavoza.backend.domain.comment.dto.*;
import com.gavoza.backend.domain.comment.entity.Comment;
import com.gavoza.backend.domain.comment.entity.ReComment;
import com.gavoza.backend.domain.comment.repository.CommentRepository;
import com.gavoza.backend.domain.comment.repository.ReCommentRepository;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.repository.PostRepository;

import com.gavoza.backend.domain.report.repository.ReportRepository;

import com.gavoza.backend.domain.user.all.entity.User;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReCommentRepository reCommentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ReCommentLikeRepository reCommentLikeRepository;
    private final AlarmRepository alarmRepository;
    private final ReportRepository reportRepository;
    private final NotificationService notificationService;

    //유저 comment 조회
    public CommentListResponse getCommentByPostId(int page, int size, Long postId, User user) {
        // postId 유효성 확인
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("해당 게시물이 존재하지 않습니다.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPages = commentRepository.findAllByPostId(postId, pageable);
        List<CommentResponseDto> commentResponseDtos = new ArrayList<>();

        for (Comment comment : commentPages) {
            List<ReCommentResponseDto> reComments = comment.getReCommentList().stream()
                    .map(reComment -> getOneReComment(reComment.getId(), user))
                    .collect(Collectors.toList());

            boolean hasLikeComment = user != null && commentLikeRepository.existsLikeByCommentAndUser(comment, user);
            boolean hasReported = reportRepository.existsReportByCommentAndUser(comment,user);

            commentResponseDtos.add(new CommentResponseDto(comment, hasLikeComment, reComments,hasReported));
        }

        return new CommentListResponse(commentResponseDtos,commentPages.getTotalPages(), commentPages.getTotalElements(), size);
    }

    //비유저 comment 조회
    public CommentListResponse getCommentByPostId2(int page, int size, Long postId) {
        // postId 유효성 확인
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("해당 게시물이 존재하지 않습니다.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPages = commentRepository.findAllByPostId(postId, pageable);
        List<CommentResponseDto> commentResponseDtos = new ArrayList<>();

        for (Comment comment : commentPages) {
            List<ReCommentResponseDto> reComments = comment.getReCommentList().stream()
                    .map(reComment -> getOneReComment2(reComment.getId()))
                    .collect(Collectors.toList());

            boolean hasLikeComment = false;
            boolean hasReported = false;

            commentResponseDtos.add(new CommentResponseDto(comment, hasLikeComment, reComments,hasReported));
        }
        return new CommentListResponse(commentResponseDtos,commentPages.getTotalPages(), commentPages.getTotalElements(), size);
    }

    @Transactional(readOnly = true)
    public ReCommentResponseDto getOneReComment(Long id, User user) {
        ReComment reComment = reCommentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        boolean reCommentHasLiked = reCommentLikeRepository.existsLikeByReCommentAndUser(reComment, user);
        boolean hasReported = reportRepository.existsReportByReCommentAndUser(reComment,user);
        return new ReCommentResponseDto(reComment, reCommentHasLiked,hasReported);
    }

    @Transactional(readOnly = true)
    public ReCommentResponseDto getOneReComment2(Long id) {
        ReComment reComment = reCommentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        boolean reCommentHasLiked = reCommentLikeRepository.existsLikeByReComment(reComment);
        boolean hasReported = reportRepository.existsReportByReComment(reComment);
        return new ReCommentResponseDto(reComment, reCommentHasLiked,hasReported);
    }

    // 댓글 생성
    public CommentResponseDto createComment(Long postId, CommentRequestDto requestDto, User user) {
        Post post = getPostById(postId);
        Comment comment = new Comment(requestDto, user.getNickname(), post, user);
        Comment newComment = commentRepository.save(comment);

        AlarmEventType eventType = AlarmEventType.NEW_COMMENT_ON_POST; // 댓글에 대한 알림 타입 설정
        Boolean isRead = false; // 초기값으로 미읽음 상태 설정
        String notificationMessage = "<b>" + user.getNickname() + "</b>" + "님이 [" + post.getContent() + "] 글에 [" + comment.getComment() + "] 댓글을 달았어요!"; // 알림 메시지 설정
        LocalDateTime registeredAt = LocalDateTime.now(); // 알림 생성 시간 설정
        String userImg = user.getProfileImageUrl();

        if (!post.getUser().getId().equals(user.getId())) {
            Alarm commentNotification = new Alarm(post ,post.getUser(), eventType, isRead, notificationMessage, registeredAt,userImg);
            alarmRepository.save(commentNotification);
        }
        notificationService.notifyAddCommentEvent(post.getUser(), post.getUser().isCommentAlarm());
        return new CommentResponseDto(newComment); // ReCommentResponseDto로 변경
    }

    // 대댓글 생성  //내가 쓴 댓글에 대댓글을 달면 알림이 안와요! 그러려면 댓글 userid 와 대댓글을 다는 현재의 userid가 똑같으면 안되겠주?
    public ReCommentResponseDto createReComment(Long commentId, ReCommentRequestDto requestDto, User user) {
        Comment comment = getCommentById(commentId);
        ReComment reComment = new ReComment(requestDto, user.getNickname(), comment, user);
        ReComment newReComment = reCommentRepository.save(reComment);

        AlarmEventType eventType = AlarmEventType.NEW_RECOMMENT_ON_POST; // 댓글에 대한 알림 타입 설정
        Boolean isRead = false; // 초기값으로 미읽음 상태 설정
        String notificationMessage ="<b>"+ user.getNickname() +"</b>"+ "님이 [" + comment.getComment() + "] 댓글에 [" + reComment.getReComment() + "] 답글을 달았어요!"; // 알림 메시지 설정
        LocalDateTime registeredAt = LocalDateTime.now(); // 알림 생성 시간 설정
        String userImg = user.getProfileImageUrl();

        if (!comment.getUser().getId().equals(user.getId())) {
            Alarm commentNotification = new Alarm(comment.getPost(),comment.getUser(), eventType, isRead, notificationMessage, registeredAt,userImg);
            alarmRepository.save(commentNotification);
        }
        notificationService.notifyAddCommentEvent(comment.getUser(), comment.getUser().isCommentAlarm());
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


