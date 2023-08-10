package com.gavoza.backend.domain.post.repository;

import com.gavoza.backend.domain.post.entity.LocationTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationTagRepository extends JpaRepository<LocationTag, Long> {

    Optional<LocationTag> findByLocationTag(String locationTagName);
}
