package com.gavoza.backend.domain.comment.service;

import com.gavoza.backend.domain.comment.entity.ReCommentLike;
import com.gavoza.backend.domain.comment.repository.ReCommentLikeRepository;
import com.gavoza.backend.domain.alarm.entity.Alarm;
import com.gavoza.backend.domain.alarm.repository.AlarmRepository;
import com.gavoza.backend.domain.alarm.service.SSENotificationService;
import com.gavoza.backend.domain.alarm.type.AlarmEventType;
import com.gavoza.backend.domain.comment.dto.request.ReCommentRequestDto;
import com.gavoza.backend.domain.comment.dto.response.ReCommentResponseDto;
import com.gavoza.backend.domain.comment.entity.Comment;
import com.gavoza.backend.domain.comment.entity.ReComment;
import com.gavoza.backend.domain.comment.repository.CommentRepository;
import com.gavoza.backend.domain.comment.repository.ReCommentRepository;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.dto.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReCommentService {
    private final CommentRepository commentRepository;
    private final ReCommentRepository reCommentRepository;
    private final AlarmRepository alarmRepository;
    private final SSENotificationService sseNotificationService;
    private final ReCommentLikeRepository reCommentLikeRepository;

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
            sseNotificationService.notifyAddEvent(comment.getUser(), comment.getUser().isCommentAlarm());
        }
        return new ReCommentResponseDto(newReComment);
    }

    // 대댓글 수정
    @Transactional
    public ReCommentResponseDto updateReComment(Long reCommentId, ReCommentRequestDto requestDto, User user) {
        ReComment reComment = getReCommentById(reCommentId);
        validateReCommentOwnership(reComment, user);
        reComment.update(requestDto);
        return new ReCommentResponseDto(reComment);
    }

    // 대댓글 삭제
    public void deleteReComment(Long reCommentId, User user) {
        ReComment reComment = getReCommentById(reCommentId);
        validateReCommentOwnership(reComment, user);
        reCommentRepository.delete(reComment);
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
    }

    private ReComment getReCommentById(Long reCommentId) {
        return reCommentRepository.findById(reCommentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대댓글입니다."));
    }

    private void validateReCommentOwnership(ReComment reComment, User user) {
        if (!reComment.getNickname().equals(user.getNickname())) {
            throw new IllegalArgumentException("댓글 작성자가 아닙니다.");
        }
    }

    //대댓글 좋아요
    public MessageResponseDto recommentLike(Long id, User user) {
        ReComment reComment = reCommentRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("해당 댓글은 존재하지 않습니다."));

        if (!reCommentLikeRepository.existsLikeByReCommentAndUser(reComment,user)){
            ReCommentLike like = new ReCommentLike(reComment,user);
            reCommentLikeRepository.save(like);

            // 좋아요 알림 생성 및 저장
            String notificationMessage = "<b>" + user.getNickname() +"</b>"+ "님이 [" + reComment.getReComment() + "] 답글에 좋아요를 눌렀어요!"; // 알림 메시지 설정
            AlarmEventType eventType = AlarmEventType.NEW_LIKE_ON_RECOMMENT; // 알림 타입 설정
            Boolean isRead = false; // 초기값으로 미읽음 상태 설정
            LocalDateTime registeredAt = LocalDateTime.now(); // 알림 생성 시간 설정
            String userImg = user.getProfileImageUrl();

            if (!reComment.getUser().getId().equals(user.getId())) {
                Alarm likeNotification = new Alarm(reComment.getComment().getPost() ,reComment.getUser(), eventType, isRead, notificationMessage, registeredAt, userImg);
                alarmRepository.save(likeNotification);
                sseNotificationService.notifyAddEvent(reComment.getUser(), reComment.getUser().isLikeAlarm());
            }
            return new MessageResponseDto("댓글 좋아요");
        }

        ReCommentLike like = reCommentLikeRepository.findByReCommentAndUser(reComment,user)
                .orElseThrow(()-> new IllegalArgumentException("좋아요에 대한 정보가 존재하지 않습니다."));
        reCommentLikeRepository.delete(like);
        return new MessageResponseDto("댓글 좋아요 취소");
    }
}

