package com.gavoza.backend.domain.user.social.controller;

import com.gavoza.backend.domain.user.all.entity.RefreshToken;
import com.gavoza.backend.domain.user.all.service.LoginService;
import com.gavoza.backend.domain.user.all.service.SignupService;
import com.gavoza.backend.domain.user.all.validator.TokenValidator;
import com.gavoza.backend.domain.user.social.dto.SocialAuthCodeRequestDto;
import com.gavoza.backend.domain.user.social.dto.SocialLoginResponseDto;
import com.gavoza.backend.domain.user.social.service.SocialService;
import com.gavoza.backend.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SocialController {

    private final SocialService socialService;
    private final JwtUtil jwtUtil;
    private final TokenValidator tokenValidator;

    @PostMapping("/login/kakao")
    public ResponseEntity<SocialLoginResponseDto> signInWithKakao(@RequestBody SocialAuthCodeRequestDto kakaoAuthCodeRequestDto) {
        String authCode = kakaoAuthCodeRequestDto.getAuthCode();

        // 인가 코드로 액세스 토큰 발급
        String accessToken = socialService.getAccessTokenFromAuthCode(authCode);

        String email = socialService.signInWithKakao(accessToken);

        // 액세스 토큰 생성
        String customAccessToken = jwtUtil.createAccessToken(email);

        // 리프레시 토큰 저장 및 생성
        RefreshToken refreshTokenEntity = tokenValidator.createAndSaveRefreshToken(email);

        // 액세스 토큰의 만료 시간 가져오기
        long expirationTime = jwtUtil.getExpirationTime(customAccessToken);

        SocialLoginResponseDto kakaoLoginResponseDto = new SocialLoginResponseDto();
        kakaoLoginResponseDto.setEmail(email);
        kakaoLoginResponseDto.setAccessToken(customAccessToken);
        kakaoLoginResponseDto.setRefreshToken(refreshTokenEntity.getToken());
        kakaoLoginResponseDto.setExpirationTime(expirationTime);
        kakaoLoginResponseDto.setMessage("카카오 로그인에 성공하셨습니다.");


        return ResponseEntity.ok(kakaoLoginResponseDto);
    }

    @PostMapping("/login/naver")
    public ResponseEntity<SocialLoginResponseDto> signInWithNaver(@RequestBody SocialAuthCodeRequestDto naverAuthCodeRequestDto) {
        String authCode = naverAuthCodeRequestDto.getAuthCode();

        // 인가 코드로 액세스 토큰 발급
        String accessToken = socialService.getAccessTokenFromNaverAuthCode(authCode);

        // 네이버 로그인
        String email = socialService.signInWithNaver(accessToken);

        // 액세스 토큰 생성
        String customAccessToken = jwtUtil.createAccessToken(email);

        // 리프레시 토큰 저장 및 생성
        RefreshToken refreshTokenEntity = tokenValidator.createAndSaveRefreshToken(email);

        // 액세스 토큰의 만료 시간 가져오기
        long expirationTime = jwtUtil.getExpirationTime(customAccessToken);

        SocialLoginResponseDto naverLoginResponseDto = new SocialLoginResponseDto();
        naverLoginResponseDto.setEmail(email);
        naverLoginResponseDto.setAccessToken(customAccessToken);
        naverLoginResponseDto.setRefreshToken(refreshTokenEntity.getToken());
        naverLoginResponseDto.setExpirationTime(expirationTime);
        naverLoginResponseDto.setMessage("네이버 로그인에 성공하셨습니다.");


        return ResponseEntity.ok(naverLoginResponseDto);
    }

    @PostMapping("/login/google")
    public ResponseEntity<SocialLoginResponseDto> signInWithGoogle(@RequestBody SocialAuthCodeRequestDto googleAuthCodeRequestDto) {
        String authCode = googleAuthCodeRequestDto.getAuthCode();

        // 인가 코드로 액세스 토큰 발급
        String accessToken = socialService.getAccessTokenFromGoogleAuthCode(authCode);

        // 구글 로그인
        String email = socialService.signInWithGoogle(accessToken);

        // 액세스 토큰 생성
        String customAccessToken = jwtUtil.createAccessToken(email);

        // 리프레시 토큰 저장 및 생성
        RefreshToken refreshTokenEntity = tokenValidator.createAndSaveRefreshToken(email);

        // 액세스 토큰의 만료 시간 가져오기
        long expirationTime = jwtUtil.getExpirationTime(customAccessToken);

        SocialLoginResponseDto googleLoginResponseDto = new SocialLoginResponseDto();
        googleLoginResponseDto.setEmail(email);
        googleLoginResponseDto.setAccessToken(customAccessToken);
        googleLoginResponseDto.setRefreshToken(refreshTokenEntity.getToken());
        googleLoginResponseDto.setExpirationTime(expirationTime);
        googleLoginResponseDto.setMessage("구글 로그인에 성공하셨습니다.");


        return ResponseEntity.ok(googleLoginResponseDto);
    }
}