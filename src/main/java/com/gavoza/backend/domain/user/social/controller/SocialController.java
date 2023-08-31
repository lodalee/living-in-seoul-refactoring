package com.gavoza.backend.domain.user.social.controller;

import com.gavoza.backend.domain.user.all.entity.RefreshToken;
import com.gavoza.backend.domain.user.all.validator.TokenValidator;
import com.gavoza.backend.domain.user.social.dto.SocialAuthCodeRequestDto;
import com.gavoza.backend.domain.user.social.dto.SocialLoginResponseDto;
import com.gavoza.backend.domain.user.social.service.SocialTokenService;
import com.gavoza.backend.domain.user.social.service.SocialSigninService;
import com.gavoza.backend.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SocialController {

    private final SocialSigninService socialSigninService;
    private final SocialTokenService socialTokenService;
    private final JwtUtil jwtUtil;
    private final TokenValidator tokenValidator;

    @PostMapping("/login/kakao")
    public ResponseEntity<SocialLoginResponseDto> signInWithKakao(@RequestBody SocialAuthCodeRequestDto kakaoAuthCodeRequestDto) {
        String authCode = kakaoAuthCodeRequestDto.getAuthCode();
        // 인가 코드로 액세스 토큰 발급
        String accessToken = socialTokenService.getAccessTokenFromAuthCode(authCode);
        String email = socialSigninService.signInWithKakao(accessToken);

        return processSocialLogin(email,"카카오 로그인에 성공하셨습니다.");
    }

    @PostMapping("/login/naver")
    public ResponseEntity<SocialLoginResponseDto> signInWithNaver(@RequestBody SocialAuthCodeRequestDto naverAuthCodeRequestDto) {
        String authCode = naverAuthCodeRequestDto.getAuthCode();
        // 인가 코드로 액세스 토큰 발급
        String accessToken = socialTokenService.getAccessTokenFromNaverAuthCode(authCode);
        // 네이버 로그인
        String email = socialSigninService.signInWithNaver(accessToken);

        return processSocialLogin(email,"네이버 로그인에 성공하셨습니다.");
    }

    @PostMapping("/login/google")
    public ResponseEntity<SocialLoginResponseDto> signInWithGoogle(@RequestBody SocialAuthCodeRequestDto googleAuthCodeRequestDto) {
        String authCode = googleAuthCodeRequestDto.getAuthCode();
        // 인가 코드로 액세스 토큰 발급
        String accessToken = socialTokenService.getAccessTokenFromGoogleAuthCode(authCode);
        // 구글 로그인
        String email = socialSigninService.signInWithGoogle(accessToken);

        return processSocialLogin(email,"구글 로그인에 성공하셨습니다.");
    }



    private ResponseEntity<SocialLoginResponseDto> processSocialLogin(String email, String successMessage) {
        // 액세스 토큰 생성
        String customAccessToken = jwtUtil.createAccessToken(email);

        // 리프레시 토큰 저장 및 생성
        RefreshToken refreshTokenEntity = tokenValidator.createAndSaveRefreshToken(email);

        // 액세스 토큰의 만료 시간 가져오기
        Date expirationDate = jwtUtil.getExpirationDateFromToken(customAccessToken);

        SocialLoginResponseDto loginResponseDto = new SocialLoginResponseDto();
        loginResponseDto.setEmail(email);
        loginResponseDto.setAccessToken(customAccessToken);
        loginResponseDto.setRefreshToken(refreshTokenEntity.getToken());
        loginResponseDto.setExpirationDate(expirationDate);
        loginResponseDto.setMessage(successMessage);

        return ResponseEntity.ok(loginResponseDto);
    }

}