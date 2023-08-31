package com.gavoza.backend.domain.user.all.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false, unique = true)
    private String userEmail;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    protected RefreshToken() {
    }

    public RefreshToken(String token, String userEmail, LocalDateTime expiryDate) {
        if (userEmail == null || userEmail.isEmpty()) {
            throw new IllegalArgumentException("사용자 이메일은 null 또는 빈 값이 될 수 없습니다.");
        }
        this.token = token;
        this.userEmail = userEmail;
        this.expiryDate = expiryDate;
    }

    public void updateToken(String newToken) {
        this.token = newToken;
    }

    public void updateExpiryDate(LocalDateTime newExpiryDate) {
        this.expiryDate = newExpiryDate;
    }
}
