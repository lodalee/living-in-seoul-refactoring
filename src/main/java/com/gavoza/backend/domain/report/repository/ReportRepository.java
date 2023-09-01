package com.gavoza.backend.domain.report.repository;

import com.gavoza.backend.domain.comment.entity.Comment;
import com.gavoza.backend.domain.comment.entity.ReComment;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.report.entity.Report;
import com.gavoza.backend.domain.user.all.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsReportByPostAndUser(Post post, User user);

    boolean existsReportByCommentAndUser(Comment comment, User user);

    boolean existsReportByReCommentAndUser(ReComment recomment, User user);

    Optional<Report> findByCommentAndUser(Comment comment, User user);

    Optional<Report> findByPostAndUser(Post post, User user);

    Optional<Report> findByReCommentAndUser(ReComment reComment, User user);

    boolean existsReportByReComment(ReComment reComment);
}
