package com.gavoza.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FavoriteLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gu; // 구

    private String dong; // 동

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public FavoriteLocation(String gu, String dong, User user) {
        this.gu = gu;
        this.dong = dong;
        this.user = user;
    }
}

