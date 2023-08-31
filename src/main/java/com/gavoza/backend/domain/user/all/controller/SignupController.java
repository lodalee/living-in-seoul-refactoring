package com.gavoza.backend.domain.user.all.controller;

import com.gavoza.backend.domain.user.all.dto.request.Step1RequestDto;
import com.gavoza.backend.domain.user.all.dto.request.Step2RequestDto;
import com.gavoza.backend.domain.user.all.dto.request.UserUpdateRequestDto;
import com.gavoza.backend.domain.user.all.dto.response.MessageResponseDto;
import com.gavoza.backend.domain.user.all.dto.response.TokenResMsgDto;
import com.gavoza.backend.domain.user.all.entity.RefreshToken;
import com.gavoza.backend.domain.user.all.service.ProfileService;
import com.gavoza.backend.domain.user.all.service.SignupService;
import com.gavoza.backend.domain.user.all.validator.TokenValidator;
import com.gavoza.backend.global.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/signup")
public class SignupController {
    private final SignupService signupService;
    private final JwtUtil jwtUtil;
    private final ProfileService profileService;
    private final TokenValidator tokenValidator;

    @PostMapping("/step1")
    public ResponseEntity<TokenResMsgDto> step1(@RequestBody Step1RequestDto requestDto) {
        try {
            String userEmail = signupService.step1(requestDto);

            // 액세스 토큰 생성
            String accessToken = jwtUtil.createAccessToken(userEmail);

            // 리프레시 토큰 저장 및 생성
            RefreshToken refreshTokenEntity = tokenValidator.createAndSaveRefreshToken(userEmail);

            // 액세스 토큰의 만료 날짜와 시간 가져오기
            Date expirationDate = jwtUtil.getExpirationDateFromToken(accessToken);

            String message = "회원가입에 성공하셨습니다.";

            TokenResMsgDto tokenResponseDto = new TokenResMsgDto(message, accessToken, refreshTokenEntity.getToken(), expirationDate);

            return ResponseEntity.ok(tokenResponseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new TokenResMsgDto(e.getMessage(), null, null, null));
        }
    }


    @PutMapping("/step2")
    public ResponseEntity<MessageResponseDto> step2(@RequestBody Step2RequestDto requestDto, HttpServletRequest request) {
        try {
            signupService.step2(request, requestDto);
            return ResponseEntity.ok(new MessageResponseDto("사용자 정보가 수정되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(e.getMessage()));
        }
    }
    @PutMapping("/update")
    public ResponseEntity<MessageResponseDto> updateUserInfo(@RequestBody UserUpdateRequestDto requestDto,
                                                             HttpServletRequest request) {
        String email = jwtUtil.getEmailFromAuthHeader(request);
        profileService.updateUserInfo(email, requestDto);
        return ResponseEntity.ok(new MessageResponseDto("회원 정보가 수정되었습니다."));
    }
    @DeleteMapping("/delete")
    public ResponseEntity<MessageResponseDto> deleteUser(HttpServletRequest request) {
        String email = jwtUtil.getEmailFromAuthHeader(request);
        signupService.delete(email);
        return ResponseEntity.ok(new MessageResponseDto("회원 탈퇴가 완료되었습니다."));
    }
}
