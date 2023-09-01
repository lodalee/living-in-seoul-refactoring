package com.gavoza.backend.domain.search.repository;

import com.gavoza.backend.domain.search.entity.SearchLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface SearchRepository extends JpaRepository<SearchLog, Long> {
    void deleteAllBySearchTimeBefore(Date localDateTime);
    // 추가적인 쿼리 메서드가 필요하면 여기에 추가 가능
}
