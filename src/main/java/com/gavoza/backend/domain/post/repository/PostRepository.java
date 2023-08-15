package com.gavoza.backend.domain.post.repository;

import com.gavoza.backend.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByLocationTagContaining(String locationTag);
    List<Post> findAllByPurposeTagContaining(String purposeTag);
    List<Post> findAll();

    Page<Post> findAllByLocationTagContaining(String locationTagName, Pageable pageable);
    Page<Post> findAllByPurposeTagContaining(String purposeTagName, Pageable pageable);
}
