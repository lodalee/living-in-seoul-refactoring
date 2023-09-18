package com.gavoza.backend.domain.user.service;

import com.gavoza.backend.domain.user.entity.FavoriteLocation;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.domain.user.repository.FavoriteLocationRepository;
import com.gavoza.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteLocationService {

    private final UserRepository userRepository;
    private final FavoriteLocationRepository favoriteLocationRepository;

    public FavoriteLocation addFavoriteLocation(String email, String gu, String dong) throws IllegalArgumentException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 이미 존재하는 즐겨찾기 위치인지 확인
        if (favoriteLocationRepository.findByUserIdAndGuAndDong(user.getId(), gu, dong).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 즐겨찾기 위치입니다.");
        }

        FavoriteLocation favoriteLocation = new FavoriteLocation(gu, dong, user);
        user.getFavoriteLocations().add(favoriteLocation);

        userRepository.save(user);

        return favoriteLocationRepository.save(favoriteLocation);
    }

    public void removeFavoriteLocation(String email, Long locationId) throws IllegalArgumentException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        FavoriteLocation favorite = favoriteLocationRepository.findByIdAndUserId(locationId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당하는 즐겨찾기 위치가 없습니다."));

        user.getFavoriteLocations().remove(favorite);

        userRepository.save(user);
    }
}
