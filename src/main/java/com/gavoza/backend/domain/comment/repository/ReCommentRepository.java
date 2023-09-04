package com.gavoza.backend.domain.comment.repository;

import com.gavoza.backend.domain.comment.entity.ReComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReCommentRepository extends JpaRepository<ReComment, Long> {
    void deleteByUserId(Long id);
}
