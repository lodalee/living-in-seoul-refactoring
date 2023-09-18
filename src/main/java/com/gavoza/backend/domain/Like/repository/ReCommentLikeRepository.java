package com.gavoza.backend.domain.Like.repository;

import com.gavoza.backend.domain.Like.entity.ReCommentLike;
import com.gavoza.backend.domain.comment.entity.ReComment;
import com.gavoza.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReCommentLikeRepository extends JpaRepository<ReCommentLike, Long> {

    boolean existsLikeByReCommentAndUser(ReComment reComment, User user);

    Optional<ReCommentLike> findByReCommentAndUser(ReComment reComment, User user);

    boolean existsLikeByReComment(ReComment reComment);

    void deleteByUserId(Long userId);
}
