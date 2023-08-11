package com.gavoza.backend.domain.tag.repository;

import com.gavoza.backend.domain.tag.entity.PurposeTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurposeTagRepository extends JpaRepository<PurposeTag, Long> {

    List<PurposeTag> findByPurposeTag(String purposeName);
}