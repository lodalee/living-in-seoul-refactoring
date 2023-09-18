package com.gavoza.backend.domain.user.repository;

import com.gavoza.backend.domain.user.entity.FavoriteLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteLocationRepository extends JpaRepository<FavoriteLocation, Long> {
    List<FavoriteLocation> findByUserId(Long userId);
    Optional<FavoriteLocation> findByIdAndUserId(Long id, Long userId);

    void deleteByUserId(Long userId);
    Optional<FavoriteLocation> findByUserIdAndGuAndDong(Long userId, String gu, String dong);

}
