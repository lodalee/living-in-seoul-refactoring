package com.gavoza.backend.domain.scrap.repository;

import com.gavoza.backend.domain.scrap.entity.PostScrap;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.user.all.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostScrapRepository extends JpaRepository<PostScrap, Long> {

    Optional<PostScrap> findByPostAndUser(Post post, User user);

    Page<PostScrap> findAllByUser(User user, Pageable pageable);

    boolean existsScrapByPostAndUser(Post post, User user);

    void deleteByUserId(Long id);
}
