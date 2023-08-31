package com.gavoza.backend.domain.user.all.repository;

import com.gavoza.backend.domain.user.all.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserEmail(String userEmail);
}
