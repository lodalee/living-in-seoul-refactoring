package com.gavoza.backend.domain.scrap.repository;

import com.gavoza.backend.domain.scrap.entity.PostScrap;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostScrapRepository extends JpaRepository<PostScrap, Long> {
    boolean existsLikeByPostAndUser(Post post, User user);

    Optional<PostScrap> findByPostAndUser(Post post, User user);

    Page<PostScrap> findAllByUser(User user, Pageable pageable);
}
