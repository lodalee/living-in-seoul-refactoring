package com.gavoza.backend.domain.post.repository;

import com.gavoza.backend.domain.tag.entity.LocationTag;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.tag.entity.PurposeTag;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    //전체조회
    List<Post> findAll();

    Optional<Post> findAllByLocationTag(Sort createdAt, LocationTag locationTag);
    Optional<Post> findAllByPurposeTag(Sort createdAt, PurposeTag purposeTag);
}
