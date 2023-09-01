package com.gavoza.backend.domain.user.all.service;

import com.gavoza.backend.domain.user.all.entity.FavoriteLocation;
import com.gavoza.backend.domain.user.all.entity.User;
import com.gavoza.backend.domain.user.all.repository.FavoriteLocationRepository;
import com.gavoza.backend.domain.user.all.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteLocationService {

    private final UserRepository userRepository;
    private final FavoriteLocationRepository favoriteLocationRepository;

    public void addFavoriteLocation(String email, String district, String neighborhood) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        FavoriteLocation favoriteLocation= new FavoriteLocation(district, neighborhood,user);
        user.getFavoriteLocations().add(favoriteLocation);

        userRepository.save(user);
    }

    public void removeFavoriteLocation(String email, Long locationId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        FavoriteLocation favorite = favoriteLocationRepository.findByIdAndUserId(locationId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당하는 즐겨찾기 위치가 없습니다."));

        user.getFavoriteLocations().remove(favorite);

        userRepository.save(user);
    }
}
