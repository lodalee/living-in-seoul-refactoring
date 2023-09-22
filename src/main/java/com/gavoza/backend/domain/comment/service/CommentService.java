package com.gavoza.backend.domain.comment.service;

import com.gavoza.backend.domain.comment.entity.Commentlike;
import com.gavoza.backend.domain.comment.repository.CommentLikeRepository;
import com.gavoza.backend.domain.comment.repository.ReCommentLikeRepository;
import com.gavoza.backend.domain.alarm.entity.Alarm;
import com.gavoza.backend.domain.alarm.repository.AlarmRepository;
import com.gavoza.backend.domain.alarm.service.SSENotificationService;
import com.gavoza.backend.domain.alarm.type.AlarmEventType;
import com.gavoza.backend.domain.comment.dto.request.CommentRequestDto;
import com.gavoza.backend.domain.comment.dto.response.CommentResponseDto;
import com.gavoza.backend.domain.comment.dto.response.CommentResultDto;
import com.gavoza.backend.domain.comment.dto.response.CommentsResponseDto;
import com.gavoza.backend.domain.comment.dto.response.ReCommentResponseDto;
import com.gavoza.backend.domain.comment.entity.Comment;
import com.gavoza.backend.domain.comment.entity.ReComment;
import com.gavoza.backend.domain.comment.repository.CommentRepository;
import com.gavoza.backend.domain.comment.repository.ReCommentRepository;
import com.gavoza.backend.domain.post.dto.response.PostUserDto;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.report.repository.ReportRepository;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.dto.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final AlarmRepository alarmRepository;
    private final ReportRepository reportRepository;
    private final SSENotificationService sseNotificationService;

    //유저 comment 조회
    public CommentsResponseDto getCommentByPostId(int page, int size, Long postId, User user) {
        // postId 유효성 확인
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("해당 게시물이 존재하지 않습니다.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPages = commentRepository.findAllByPostId(postId, pageable);

        List<CommentResultDto> commentResultDtos = commentPages.stream()
                .map(comment -> mapToCommentResultDto(comment, user))
                .collect(Collectors.toList());

        return new CommentsResponseDto("댓글 조회 성공", commentPages.getTotalPages(),
                commentPages.getTotalElements(), size, commentResultDtos);
    }

    private CommentResultDto mapToCommentResultDto(Comment comment, User user) {
        PostUserDto postUserDto = new PostUserDto(comment.getUser());
        CommentResponseDto commentResponseDto = new CommentResponseDto(comment);

        List<ReCommentResponseDto> reComments = comment.getReCommentList().stream()
                .map(recomment -> getOneReComment(recomment.getId(), user))
                .collect(Collectors.toList());

        // Set the reComments list to the comment response DTO
        commentResponseDto.setReComments(reComments);

        if (Objects.isNull(user)) {
            return new CommentResultDto(postUserDto,commentResponseDto ,false,false);
        }

        boolean commentHasLiked = commentLikeRepository.existsLikeByCommentAndUser(comment, user);
        boolean hasReported = reportRepository.existsReportByCommentAndUser(comment,user);

        return new CommentResultDto(postUserDto,commentResponseDto ,commentHasLiked ,hasReported);
    }

    @Transactional
    public ReCommentResponseDto getOneReComment(Long id, User user) {
        ReComment reComment = reCommentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        if (Objects.isNull(user)) {
            return new ReCommentResponseDto(reComment, false,false);
        }

        boolean reCommentHasLiked = reCommentLikeRepository.existsLikeByReCommentAndUser(reComment, user);
        boolean hasReported = reportRepository.existsReportByReCommentAndUser(reComment,user);
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
            Alarm commentNotification = new Alarm(post ,post.getUser(), eventType, isRead, notificationMessage, registeredAt, userImg);
            alarmRepository.save(commentNotification);
            sseNotificationService.notifyAddEvent(post.getUser(), post.getUser().isCommentAlarm());
        }
        return new CommentResponseDto(newComment); // ReCommentResponseDto로 변경
    }
    // 댓글 수정
    @Transactional
    public CommentResponseDto updateComment(Long commentId, CommentRequestDto requestDto, User user) {
        Comment comment = getCommentById(commentId);
        validateCommentOwnership(comment, user);
        comment.update(requestDto);
        return new CommentResponseDto(comment); // ReCommentResponseDto로 변경
    }

    // 댓글 삭제
    public void deleteComment(Long commentId, User user) {
        Comment comment = getCommentById(commentId);
        validateCommentOwnership(comment, user);
        commentRepository.delete(comment);
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글 입니다."));
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
    }

    private void validateCommentOwnership(Comment comment, User user) {
        if (!comment.getNickname().equals(user.getNickname())) {
            throw new IllegalArgumentException("댓글 작성자가 아닙니다.");
        }
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
                sseNotificationService.notifyAddEvent(comment.getUser(), comment.getUser().isLikeAlarm());
            }
            return new MessageResponseDto("댓글 좋아요");
        }

        Commentlike like = commentLikeRepository.findByCommentAndUser(comment,user)
                .orElseThrow(
                        ()-> new IllegalArgumentException("좋아요에 대한 정보가 존재하지 않습니다."));
        commentLikeRepository.delete(like);
        return new MessageResponseDto("댓글 좋아요 취소");
    }
}


