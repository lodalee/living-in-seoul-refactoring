package com.gavoza.backend.domain.post.repository;

import com.gavoza.backend.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllBycategory(String category);

    Page<Post> findAllByCategoryAndHashtagContainingOrderByPostViewCountDesc(String category, String hashtagName, Pageable pageable);

    Page<Post> findAllByCategoryAndHashtagContainingOrderByCreatedAtDesc(String category, String hashtagName, Pageable pageable);

    List<Post> findAllByHashtagContainingOrderByPostViewCountDesc(String hashtagName);
    List<Post> findAllByHashtagContainingOrderByCreatedAtDesc(String hashtagName);

}
