package com.gavoza.backend.domain.user.all.controller;

import com.gavoza.backend.domain.user.all.dto.request.AddFavoriteRequestDto;
import com.gavoza.backend.domain.user.all.dto.request.FavoriteLocationRequestDto;
import com.gavoza.backend.domain.user.all.dto.response.MessageResponseDto;
import com.gavoza.backend.domain.user.all.entity.FavoriteLocation;
import com.gavoza.backend.domain.user.all.service.FavoriteLocationService;
import com.gavoza.backend.global.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorite-location")
public class FavoriteLocationController {
    private final JwtUtil jwtUtil;
    private final FavoriteLocationService favoriteLocationService;
    @PostMapping("/add")
    public ResponseEntity<MessageResponseDto> addFavorite(HttpServletRequest request,
                                                          @RequestBody AddFavoriteRequestDto dto) {

        String email = jwtUtil.getEmailFromAuthHeader(request);

        // 찜한 위치 객체 받아오기
        FavoriteLocation added = favoriteLocationService.addFavoriteLocation(email, dto.getGu(), dto.getDong());



        return ResponseEntity.ok(new MessageResponseDto("찜한 위치가 추가되었습니다. ID: " + added.getId()));
    }

    @DeleteMapping("/delete/{locationId}")
    public ResponseEntity<MessageResponseDto> removeFavoriteLocation(HttpServletRequest request, @PathVariable Long locationId) {
        String email = jwtUtil.getEmailFromAuthHeader(request);
        favoriteLocationService.removeFavoriteLocation(email, locationId);
        return ResponseEntity.ok(new MessageResponseDto("찜한 위치가 삭제되었습니다."));
    }
}
