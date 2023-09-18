package com.gavoza.backend.domain.user.controller;

import com.gavoza.backend.domain.user.dto.request.UserEditRequestDto;
import com.gavoza.backend.global.dto.MessageResponseDto;
import com.gavoza.backend.domain.user.service.ProfileEditService;
import com.gavoza.backend.domain.user.service.SignupService;
import com.gavoza.backend.global.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserEditController {
    private final SignupService signupService;
    private final JwtUtil jwtUtil;
    private final ProfileEditService profileEditService;



    @PutMapping("/update")
    public ResponseEntity<MessageResponseDto> updateUserInfo(@RequestBody UserEditRequestDto requestDto,
                                                             HttpServletRequest request) {
        String email = jwtUtil.getEmailFromAuthHeader(request);
        profileEditService.updateUserInfo(email, requestDto);
        return ResponseEntity.ok(new MessageResponseDto("회원 정보가 수정되었습니다."));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<MessageResponseDto> deleteUser(HttpServletRequest request) {
        String email = jwtUtil.getEmailFromAuthHeader(request);
        signupService.delete(email);
        return ResponseEntity.ok(new MessageResponseDto("회원 탈퇴가 완료되었습니다."));
    }
}
