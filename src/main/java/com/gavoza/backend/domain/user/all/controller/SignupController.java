package com.gavoza.backend.domain.user.all.controller;

import com.gavoza.backend.domain.user.all.dto.request.UserUpdateRequestDto;
import com.gavoza.backend.domain.user.all.dto.response.MessageResponseDto;
import com.gavoza.backend.domain.user.all.service.ProfileService;
import com.gavoza.backend.domain.user.all.service.SignupService;
import com.gavoza.backend.global.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class SignupController {
    private final SignupService signupService;
    private final JwtUtil jwtUtil;
    private final ProfileService profileService;



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
