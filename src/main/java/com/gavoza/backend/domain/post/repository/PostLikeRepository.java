package com.gavoza.backend.domain.post.repository;

import com.gavoza.backend.domain.post.entity.Postlike;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<Postlike, Long> {
    boolean existsLikeByPostAndUser(Post post, User user);

    Optional<Postlike> findByPostAndUser(Post post, User user);

    void deleteByUserId(Long userId);
}
