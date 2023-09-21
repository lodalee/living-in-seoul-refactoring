package com.gavoza.backend.domain.auth.controller;

import com.gavoza.backend.domain.auth.dto.request.RefreshTokenRequestDto;
import com.gavoza.backend.domain.auth.dto.response.RefreshResMsgDto;
import com.gavoza.backend.domain.auth.validator.TokenValidator;
import com.gavoza.backend.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class RefreshtokenController {

    private final TokenValidator tokenValidator;
    private final JwtUtil jwtUtil;

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResMsgDto> refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        String refreshTokenValue = refreshTokenRequestDto.getRefreshToken();

        try {
            // 유효성 검사 후 이메일 반환
            String email = tokenValidator.validateRefreshTokenAndReturnEmail(refreshTokenValue);

            // 액세스 토큰 생성
            String newAccessToken = jwtUtil.createAccessToken(email);

            // 액세스 토큰의 만료 날짜와 시간 가져오기
            Date expirationDate = jwtUtil.getExpirationDateFromToken(newAccessToken);

            // 토큰 응답
            RefreshResMsgDto refreshResMsgDto =
                    new RefreshResMsgDto("Access 토큰 발급 성공", newAccessToken, expirationDate);
            return ResponseEntity.ok().body(refreshResMsgDto);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RefreshResMsgDto(e.getMessage(), null, null));
        }
    }
}
