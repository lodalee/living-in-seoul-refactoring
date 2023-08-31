package com.gavoza.backend.domain.report.entity;

import com.gavoza.backend.domain.comment.entity.Comment;
import com.gavoza.backend.domain.comment.entity.ReComment;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.report.ReportType;
import com.gavoza.backend.domain.user.all.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recomment_id")
    private ReComment reComment;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime reportedAt; // 스크랩한 시간을 저장할 필드

    public Report(Post post, User user, LocalDateTime reportedAt, ReportType reportType) {
        this.post = post;
        this.user = user;
        this.reportedAt = reportedAt;
        this.reportType = reportType;
    }

    public Report(Comment comment, User user, LocalDateTime reportedAt, ReportType reportType) {
        this.comment = comment;
        this.user = user;
        this.reportedAt = reportedAt;
        this.reportType = reportType;
    }

    public Report(ReComment reComment, User user, LocalDateTime reportedAt, ReportType reportType) {
        this.reComment = reComment;
        this.user = user;
        this.reportedAt = reportedAt;
        this.reportType = reportType;
    }
}
