package com.gavoza.backend.domain.post.repository;

import com.gavoza.backend.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllBycategory(String category);

    List<Post> findAllByCategoryAndHashtagContainingOrderByPostViewCountDesc(String category, String hashtagName);

    List<Post> findAllByCategoryAndHashtagContainingOrderByCreatedAtDesc(String category, String hashtagName);

    List<Post> findAllByHashtagContainingOrderByPostViewCountDesc(String hashtagName);
    List<Post> findAllByHashtagContainingOrderByCreatedAtDesc(String hashtagName);

}
