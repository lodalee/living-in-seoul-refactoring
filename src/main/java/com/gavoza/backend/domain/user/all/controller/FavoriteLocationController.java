package com.gavoza.backend.domain.user.all.controller;

import com.gavoza.backend.domain.user.all.dto.request.FavoriteLocationRequestDto;
import com.gavoza.backend.domain.user.all.dto.response.MessageResponseDto;
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
    public ResponseEntity<MessageResponseDto> addFavoriteLocation(HttpServletRequest request, @RequestBody FavoriteLocationRequestDto dto) {
        String email = jwtUtil.getEmailFromAuthHeader(request);
        favoriteLocationService.addFavoriteLocation(email, dto.getGu(), dto.getDong());
        return ResponseEntity.ok(new MessageResponseDto("찜한 위치가 추가되었습니다."));
    }

    @DeleteMapping("/delete/{locationId}")
    public ResponseEntity<MessageResponseDto> removeFavoriteLocation(HttpServletRequest request, @PathVariable Long locationId) {
        String email = jwtUtil.getEmailFromAuthHeader(request);
        favoriteLocationService.removeFavoriteLocation(email, locationId);
        return ResponseEntity.ok(new MessageResponseDto("찜한 위치가 삭제되었습니다."));
    }
}
