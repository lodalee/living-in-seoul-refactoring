package com.gavoza.backend.domain.post.repository;

import com.gavoza.backend.domain.post.entity.PostSearchLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface PostSearchRepository extends JpaRepository<PostSearchLog, Long> {
    void deleteAllBySearchTimeBefore(Date localDateTime);
    // 추가적인 쿼리 메서드가 필요하면 여기에 추가 가능
}
