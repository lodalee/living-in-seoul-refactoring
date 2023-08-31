package com.gavoza.backend.domain.report.service;

import com.gavoza.backend.domain.comment.entity.Comment;
import com.gavoza.backend.domain.comment.entity.ReComment;
import com.gavoza.backend.domain.comment.repository.CommentRepository;
import com.gavoza.backend.domain.comment.repository.ReCommentRepository;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.report.ReportType;
import com.gavoza.backend.domain.report.entity.Report;
import com.gavoza.backend.domain.report.repository.ReportRepository;

import com.gavoza.backend.domain.user.all.entity.User;
import com.gavoza.backend.domain.user.ToPost.MessageResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReCommentRepository reCommentRepository;

    public MessageResponseDto report(Long targetId, ReportType reportType, User user) {
        if (reportType == ReportType.POST_REPORT) {
            return reportPost(targetId, user);
        } else if (reportType == ReportType.COMMENT_REPORT) {
            return reportComment(targetId, user);
        } else if (reportType == ReportType.RECOMMENT_REPORT) {
            return reportReComment(targetId, user);
        }

        throw new IllegalArgumentException("지원하지 않는 신고 유형입니다.");
    }

    private MessageResponseDto reportPost(Long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글은 존재하지 않습니다.")
        );

        if (post.getUser().getId().equals(user.getId())) {
            return new MessageResponseDto("자신의 게시물은 신고할 수 없습니다.");
        }

        if (!reportRepository.existsReportByPostAndUser(post, user)) {
            LocalDateTime reportedAt = LocalDateTime.now();
            Report report = new Report(post, user, reportedAt, ReportType.POST_REPORT);
            reportRepository.save(report);
            return new MessageResponseDto("게시물을 신고하였습니다.");
        }

        Report report = reportRepository.findByPostAndUser(post, user).orElseThrow(
                () -> new IllegalArgumentException("게시물 신고 정보가 존재하지 않습니다.")
        );
        reportRepository.delete(report);
        return new MessageResponseDto("게시물 신고를 취소하였습니다.");
    }

    private MessageResponseDto reportComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글은 존재하지 않습니다.")
        );

        if (comment.getUser().getId().equals(user.getId())) {
            return new MessageResponseDto("자신의 댓글은 신고할 수 없습니다.");
        }

        if (!reportRepository.existsReportByCommentAndUser(comment, user)) {
            LocalDateTime reportedAt = LocalDateTime.now();
            Report report = new Report(comment, user, reportedAt, ReportType.COMMENT_REPORT);
            reportRepository.save(report);
            return new MessageResponseDto("댓글을 신고하였습니다.");
        }

        Report report = reportRepository.findByCommentAndUser(comment, user).orElseThrow(
                () -> new IllegalArgumentException("댓글 신고 정보가 존재하지 않습니다.")
        );
        reportRepository.delete(report);
        return new MessageResponseDto("댓글 신고를 취소하였습니다.");
    }

    private MessageResponseDto reportReComment(Long recommentId, User user) {
        ReComment recomment = reCommentRepository.findById(recommentId).orElseThrow(
                () -> new IllegalArgumentException("해당 리코멘트는 존재하지 않습니다.")
        );

        if (recomment.getUser().getId().equals(user.getId())) {
            return new MessageResponseDto("자신의 게시물은 신고할 수 없습니다.");
        }

        if (!reportRepository.existsReportByReCommentAndUser(recomment, user)) {
            LocalDateTime reportedAt = LocalDateTime.now();
            Report report = new Report(recomment, user, reportedAt, ReportType.RECOMMENT_REPORT);
            reportRepository.save(report);
            return new MessageResponseDto("리코멘트를 신고하였습니다.");
        }

        Report report = reportRepository.findByReCommentAndUser(recomment, user).orElseThrow(
                () -> new IllegalArgumentException("리코멘트 신고 정보가 존재하지 않습니다.")
        );
        reportRepository.delete(report);
        return new MessageResponseDto("리코멘트 신고를 취소하였습니다.");
    }
}