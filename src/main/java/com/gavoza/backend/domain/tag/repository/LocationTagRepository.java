package com.gavoza.backend.domain.tag.repository;

import com.gavoza.backend.domain.tag.entity.LocationTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationTagRepository extends JpaRepository<LocationTag, Long> {

    List<LocationTag> findByLocationTag(String locationTagName);
}
