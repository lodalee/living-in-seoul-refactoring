package com.gavoza.backend.domain.user.repository;

import com.gavoza.backend.domain.user.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByGuAndDong(String gu, String dong);

    List<Location> findByGu(String gu);
}
