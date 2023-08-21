package com.gavoza.backend.domain.user.controller;

import com.gavoza.backend.domain.user.dto.*;
import com.gavoza.backend.domain.user.entity.RefreshToken;
import com.gavoza.backend.domain.user.service.UserService;
import com.gavoza.backend.global.exception.MessageResponseDto;
import com.gavoza.backend.global.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
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

    @PostMapping("/signup1")
    public ResponseEntity<MessageResponseDto> signup(@RequestBody SignupRequestDto requestDto) {
        try {
            userService.signup(requestDto);
            return ResponseEntity.ok(new MessageResponseDto("회원가입에 성공하셨습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(e.getMessage()));
        }
    }



    @GetMapping("/refresh")
    public ResponseEntity<MessageResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        String refreshTokenValue = refreshTokenRequestDto.getRefreshToken();

        try {
            // 유효성 검사 후 이메일 반환
            String email = userService.validateRefreshTokenAndReturnEmail(refreshTokenValue);

            // 액세스 토큰 생성
            String newAccessToken = jwtUtil.createAccessToken(email);

            // 토큰 응답
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(JwtUtil.AUTHORIZATION_HEADER, JwtUtil.BEARER_PREFIX + newAccessToken);
            MessageResponseDto messageResponseDto = new MessageResponseDto("Access 토큰 발급 성공");
            return ResponseEntity.ok().headers(responseHeaders).body(messageResponseDto);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponseDto("refreshToken이 만료되었습니다."));
        }
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

    @PutMapping("/signup2")
    public ResponseEntity<MessageResponseDto> updateUser(@RequestBody UpdateUserRequestDto requestDto, HttpServletRequest request) {
        try {
            String email = jwtUtil.getEmailFromAuthHeader(request);
            userService.updateUser(email, requestDto);
            return ResponseEntity.ok(new MessageResponseDto("사용자 정보가 수정되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(e.getMessage()));
        }
    }







}