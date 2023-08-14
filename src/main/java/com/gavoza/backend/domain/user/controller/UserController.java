package com.gavoza.backend.domain.user.controller;

import com.gavoza.backend.domain.user.dto.LoginRequestDto;
import com.gavoza.backend.domain.user.dto.RefreshTokenRequestDto;
import com.gavoza.backend.domain.user.dto.SignupRequestDto;
import com.gavoza.backend.domain.user.dto.TokenResponseDto;
import com.gavoza.backend.domain.user.entity.RefreshToken;
import com.gavoza.backend.domain.user.service.UserService;
import com.gavoza.backend.global.exception.MessageResponseDto;
import com.gavoza.backend.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<MessageResponseDto> signup(@RequestBody SignupRequestDto requestDto) {
        userService.signup(requestDto);
        return ResponseEntity.ok(new MessageResponseDto("회원가입에 성공하셨습니다."));
    }


    @PostMapping("/refresh")
    public ResponseEntity<MessageResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        String refreshTokenValue = refreshTokenRequestDto.getRefreshToken();

        // 유효성 검사 후 이메일 반환
        String email = userService.validateRefreshTokenAndReturnEmail(refreshTokenValue);

        // 액세스 토큰 생성
        String newAccessToken = jwtUtil.createAccessToken(email);

        // 토큰 응답
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(JwtUtil.AUTHORIZATION_HEADER, JwtUtil.BEARER_PREFIX + newAccessToken);
        MessageResponseDto messageResponseDto = new MessageResponseDto("Access 토큰 생성 성공");
        return ResponseEntity.ok().headers(responseHeaders).body(messageResponseDto);
    }


    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        // 로그인 검사 후 이메일 반환
        String email = userService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        // 액세스 토큰 생성
        String accessToken = jwtUtil.createAccessToken(email);

        // 리프레시 토큰 저장 및 생성
        RefreshToken refreshTokenEntity = userService.createAndSaveRefreshToken(email);
        String refreshToken = refreshTokenEntity.getToken();

        // 토큰 응답
        String message = "로그인에 성공하셨습니다.";
        TokenResponseDto tokenResponseDto = new TokenResponseDto(accessToken, refreshToken, message);

        return ResponseEntity.ok(tokenResponseDto);
    }
}