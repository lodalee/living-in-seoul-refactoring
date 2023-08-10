package com.gavoza.backend.domain.post.repository;

import com.gavoza.backend.domain.post.entity.PurposeTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurposeTagRepository extends JpaRepository<PurposeTag, Long> {

    Optional<PurposeTag> findByPurposeTag(String purposeTagName);
}
