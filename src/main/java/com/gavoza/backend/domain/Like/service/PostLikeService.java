package com.gavoza.backend.domain.Like.service;

import com.gavoza.backend.domain.Like.entity.Commentlike;
import com.gavoza.backend.domain.Like.entity.Postlike;
import com.gavoza.backend.domain.Like.entity.ReCommentLike;
import com.gavoza.backend.domain.Like.repository.CommentLikeRepository;
import com.gavoza.backend.domain.Like.repository.PostLikeRepository;
import com.gavoza.backend.domain.Like.repository.ReCommentLikeRepository;
import com.gavoza.backend.domain.alarm.AlarmEventType;
import com.gavoza.backend.domain.alarm.entity.Alarm;
import com.gavoza.backend.domain.alarm.repository.AlarmRepository;
import com.gavoza.backend.domain.comment.entity.Comment;
import com.gavoza.backend.domain.comment.entity.ReComment;
import com.gavoza.backend.domain.comment.repository.CommentRepository;
import com.gavoza.backend.domain.comment.repository.ReCommentRepository;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.user.all.entity.User;
import com.gavoza.backend.domain.user.all.dto.response.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final ReCommentLikeRepository reCommentLikeRepository;
    private final ReCommentRepository reCommentRepository;
    private final AlarmRepository alarmRepository;

    //post 좋아요
    public MessageResponseDto postLike(Long postId, User user){
        Post post = postRepository.findById(postId).orElseThrow(
                ()-> new IllegalArgumentException("해당 게시글은 존재하지 않습니다.")
        );

        if (!postLikeRepository.existsLikeByPostAndUser(post, user)){
            Postlike like = new Postlike(post, user) ;
            postLikeRepository.save(like);

            // 좋아요 알림 생성 및 저장
            String notificationMessage = "<b>" + user.getNickname() + "</b>" + "님이 [" + post.getContent() + "] 글에 좋아요를 눌렀어요!"; // 알림 메시지 설정
            AlarmEventType eventType = AlarmEventType.NEW_LIKE_ON_POST; // 알림 타입 설정
            Boolean isRead = false; // 초기값으로 미읽음 상태 설정
            LocalDateTime registeredAt = LocalDateTime.now(); // 알림 생성 시간 설정
            String userImg = user.getProfileImageUrl();

            if (!post.getUser().getId().equals(user.getId())) {
                Alarm likeNotification = new Alarm(post, post.getUser(), eventType, isRead, notificationMessage, registeredAt, userImg);
                alarmRepository.save(likeNotification);
            }
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

            // 좋아요 알림 생성 및 저장
            String notificationMessage ="<b>" + user.getNickname() +"</b>"+ "님이 [" + comment.getComment() + "] 댓글에 좋아요를 눌렀어요!"; // 알림 메시지 설정
            AlarmEventType eventType = AlarmEventType.NEW_LIKE_ON_COMMENT; // 알림 타입 설정
            Boolean isRead = false; // 초기값으로 미읽음 상태 설정
            LocalDateTime registeredAt = LocalDateTime.now(); // 알림 생성 시간 설정
            String userImg = user.getProfileImageUrl();

            if (!comment.getUser().getId().equals(user.getId())) {
                Alarm likeNotification = new Alarm(comment.getPost(), comment.getUser(), eventType, isRead, notificationMessage, registeredAt,userImg);
                alarmRepository.save(likeNotification);
            }
            return new MessageResponseDto("댓글 좋아요");
        }

        Commentlike like = commentLikeRepository.findByCommentAndUser(comment,user)
                .orElseThrow(
                        ()-> new IllegalArgumentException("좋아요에 대한 정보가 존재하지 않습니다."));
        commentLikeRepository.delete(like);
        return new MessageResponseDto("댓글 좋아요 취소");
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
            }
            return new MessageResponseDto("댓글 좋아요");
        }

        ReCommentLike like = reCommentLikeRepository.findByReCommentAndUser(reComment,user)
                .orElseThrow(()-> new IllegalArgumentException("좋아요에 대한 정보가 존재하지 않습니다."));
        reCommentLikeRepository.delete(like);
        return new MessageResponseDto("댓글 좋아요 취소");
    }
}