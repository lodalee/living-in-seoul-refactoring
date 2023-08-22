package com.gavoza.backend.domain.post.repository;

import com.gavoza.backend.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllBycategoryAndGuAndDong(String category, String gu, String dong);

    Page<Post> findAllByCategoryAndHashtagContainingAndGuAndDongOrderByPostViewCountDesc(String category, String hashtagName, Pageable pageable, String gu, String dong);

    Page<Post> findAllByCategoryAndHashtagContainingAndGuAndDongOrderByCreatedAtDesc(String category, String hashtagName, Pageable pageable, String gu, String dong);

    Page<Post> findAllByHashtagContainingAndGuAndDongOrderByPostViewCountDesc(String hashtagName, Pageable pageable,String gu, String dong);
    Page<Post> findAllByHashtagContainingAndGuAndDongOrderByCreatedAtDesc(String hashtagName, Pageable pageable, String gu, String dong);

    List<Post> findAllByGuAndDong(String gu, String dong);
}
