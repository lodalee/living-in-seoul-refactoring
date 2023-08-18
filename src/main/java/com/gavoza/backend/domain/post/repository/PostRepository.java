package com.gavoza.backend.domain.post.repository;

import com.gavoza.backend.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

//    List<Post> findAllByHashtagContaining(String hashtag);
//    List<Post> findAll();

//    Page<Post> findAllByHashtagContaining(String hashtagName, Pageable pageable);

//    List<Post> findAll(String content);

    List<Post> findAllBycategory(String category);

    List<Post> findAllByHashtagContaining(String hashTagName);

    List<Post> findAllByCategoryAndHashtag(String hashtagName, String category);
}
