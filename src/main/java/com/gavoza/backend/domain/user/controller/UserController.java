package com.gavoza.backend.domain.user.controller;

import com.gavoza.backend.domain.user.dto.SignupRequestDto;
import com.gavoza.backend.domain.user.service.UserService;
import com.gavoza.backend.global.exception.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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