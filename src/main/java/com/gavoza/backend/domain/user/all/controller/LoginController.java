package com.gavoza.backend.domain.user.all.controller;

import com.gavoza.backend.domain.user.all.dto.request.LoginRequestDto;
import com.gavoza.backend.domain.user.all.dto.response.MessageResponseDto;
import com.gavoza.backend.domain.user.all.dto.response.TokenResMsgDto;
import com.gavoza.backend.domain.user.all.entity.RefreshToken;
import com.gavoza.backend.domain.user.all.service.LoginService;
import com.gavoza.backend.domain.user.all.validator.TokenValidator;
import com.gavoza.backend.global.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LoginController {

    private final TokenValidator tokenValidator;
    private final LoginService loginService;
    private final JwtUtil jwtUtil;


    @PostMapping("/login")
    public ResponseEntity<TokenResMsgDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        String userEmail = loginService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        // 액세스 토큰 생성
        String accessToken = jwtUtil.createAccessToken(userEmail);

        // 리프레시 토큰 저장 및 생성
        RefreshToken refreshTokenEntity = tokenValidator.createAndSaveRefreshToken(userEmail);

        // 액세스 토큰의 만료 날짜와 시간 가져오기
        Date expirationDate = jwtUtil.getExpirationDateFromToken(accessToken);

        String message = "로그인에 성공하셨습니다.";

        TokenResMsgDto tokenResponseDto = new TokenResMsgDto(message, accessToken, refreshTokenEntity.getToken(), expirationDate);

        return ResponseEntity.ok(tokenResponseDto);
    }


    @PostMapping("/logout")
    public ResponseEntity<MessageResponseDto> logout(HttpServletRequest request) {
        String email = jwtUtil.getEmailFromAuthHeader(request);
        loginService.logout(email);
        return ResponseEntity.ok(new MessageResponseDto("로그아웃에 성공하셨습니다."));
    }


}