package com.gavoza.backend.domain.user.controller;

import com.gavoza.backend.domain.user.dto.*;
import com.gavoza.backend.domain.user.entity.RefreshToken;
import com.gavoza.backend.domain.user.service.UserService;
import com.gavoza.backend.global.exception.MessageResponseDto;
import com.gavoza.backend.global.exception.RefreshResMsgDto;
import com.gavoza.backend.global.exception.TokenResMsgDto;
import com.gavoza.backend.global.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup1")
    public ResponseEntity<MessageResponseDto> signup1(@RequestBody Signup1RequestDto requestDto) {
        try {
            userService.signup1(requestDto);
            return ResponseEntity.ok(new MessageResponseDto("회원가입에 성공하셨습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(e.getMessage()));
        }
    }



    @GetMapping("/refresh")
    public ResponseEntity<RefreshResMsgDto> refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        String refreshTokenValue = refreshTokenRequestDto.getRefreshToken();

        try {
            // 유효성 검사 후 이메일 반환
            String email = userService.validateRefreshTokenAndReturnEmail(refreshTokenValue);

            // 액세스 토큰 생성
            String newAccessToken = jwtUtil.createAccessToken(email);

            // 토큰 응답
            RefreshResMsgDto refreshResMsgDto = new RefreshResMsgDto("Access 토큰 발급 성공", newAccessToken);
            return ResponseEntity.ok().body(refreshResMsgDto);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RefreshResMsgDto(e.getMessage(), null));
        }
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        String userEmail = userService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        // 액세스 토큰 생성
        String accessToken = jwtUtil.createAccessToken(userEmail);

        // 리프레시 토큰 저장 및 생성
        RefreshToken refreshTokenEntity = userService.createAndSaveRefreshToken(userEmail);
        String refreshToken = refreshTokenEntity.getToken();

        // 토큰 응답
        String message = "로그인에 성공하셨습니다.";
        TokenResMsgDto tokenResponseDto = new TokenResMsgDto(message, accessToken, refreshToken);

        return ResponseEntity.ok(tokenResponseDto);
    }

    @PutMapping("/signup2")
    public ResponseEntity<MessageResponseDto> signup2(@RequestBody Signup2RequestDto requestDto, HttpServletRequest request) {
        try {
            userService.signup2(request, requestDto);
            return ResponseEntity.ok(new MessageResponseDto("사용자 정보가 수정되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<MessageResponseDto> updateUserInfo(@RequestBody UserUpdateRequestDto requestDto,
                                                             HttpServletRequest request) {
        String email = jwtUtil.getEmailFromAuthHeader(request);
        userService.updateUserInfo(email, requestDto);
        return ResponseEntity.ok(new MessageResponseDto("회원 정보가 수정되었습니다."));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<MessageResponseDto> deleteUser(HttpServletRequest request) {
        String email = jwtUtil.getEmailFromAuthHeader(request);
        userService.deleteUser(email);
        return ResponseEntity.ok(new MessageResponseDto("회원 탈퇴가 완료되었습니다."));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponseDto> logout(HttpServletRequest request) {
        String email = jwtUtil.getEmailFromAuthHeader(request);
        userService.logout(email);
        return ResponseEntity.ok(new MessageResponseDto("로그아웃에 성공하셨습니다."));
    }

    @PostMapping("/login/kakao")
    public ResponseEntity<?> signInWithKakao(@RequestBody KakaoAuthCodeRequestDto kakaoAuthCodeRequestDto) {
        String authCode = kakaoAuthCodeRequestDto.getAuthCode();

        // 인가 코드로 액세스 토큰 발급
        String accessToken = userService.getAccessTokenFromAuthCode(authCode);

        String email = userService.signInWithKakao(accessToken);

        // 액세스 토큰 생성
        String customAccessToken = jwtUtil.createAccessToken(email);

        // 리프레시 토큰 저장 및 생성
        RefreshToken refreshTokenEntity = userService.createAndSaveRefreshToken(email);

        KakaoLoginResponseDto loginResponseDto = new KakaoLoginResponseDto();
        loginResponseDto.setEmail(email);
        loginResponseDto.setAccessToken(customAccessToken);
        loginResponseDto.setRefreshToken(refreshTokenEntity.getToken());

        return ResponseEntity.ok(loginResponseDto);
    }

    @PostMapping("/login/google")
    public ResponseEntity<?> signInWithGoogle(@AuthenticationPrincipal OAuth2AuthenticationToken authentication) {
        String email = userService.signInWithGoogle(authentication);

        // 액세스 토큰 생성
        String customAccessToken = jwtUtil.createAccessToken(email);

        // 리프레시 토큰 저장 및 생성
        RefreshToken refreshTokenEntity = userService.createAndSaveRefreshToken(email);

        // 토큰 응답
        String message = "구글 로그인에 성공하셨습니다.";
        TokenResMsgDto tokenResponseDto = new TokenResMsgDto(message, customAccessToken, refreshTokenEntity.getToken());

        return ResponseEntity.ok(tokenResponseDto);
    }

    @PostMapping("/login/naver")
    public ResponseEntity<?> signInWithNaver(@RequestBody NaverAuthCodeRequestDto naverAuthCodeRequestDto) {
        String authCode = naverAuthCodeRequestDto.getAuthCode();

        // 인가 코드로 액세스 토큰 발급
        String accessToken = userService.getAccessTokenFromNaverAuthCode(authCode);

        // 네이버 로그인
        String email = userService.signInWithNaver(accessToken);

        // 액세스 토큰 생성
        String customAccessToken = jwtUtil.createAccessToken(email);

        // 리프레시 토큰 저장 및 생성
        RefreshToken refreshTokenEntity = userService.createAndSaveRefreshToken(email);

        // 토큰 응답
        String message = "네이버 로그인에 성공하셨습니다.";
        TokenResMsgDto tokenResponseDto = new TokenResMsgDto(message, customAccessToken, refreshTokenEntity.getToken());

        return ResponseEntity.ok(tokenResponseDto);
    }
}