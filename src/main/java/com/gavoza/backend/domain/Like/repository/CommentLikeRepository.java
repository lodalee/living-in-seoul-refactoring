package com.gavoza.backend.domain.Like.repository;

import com.gavoza.backend.domain.Like.entity.Commentlike;
import com.gavoza.backend.domain.comment.entity.Comment;
import com.gavoza.backend.domain.user.all.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<Commentlike, Long> {

    Optional <Commentlike> findByCommentAndUser(Comment comment, User user);

    boolean existsLikeByCommentAndUser(Comment comment, User user);
}
