package com.gavoza.backend.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gavoza.backend.domain.user.dto.SignupRequestDto;
import com.gavoza.backend.domain.user.service.UserService;
import com.gavoza.backend.global.exception.MessageResponseDto;
import com.gavoza.backend.global.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public MessageResponseDto signup(@RequestBody SignupRequestDto requestDto) {
        userService.signup(requestDto);
        return new MessageResponseDto("회원가입에 성공하셨습니다.");
    }

}