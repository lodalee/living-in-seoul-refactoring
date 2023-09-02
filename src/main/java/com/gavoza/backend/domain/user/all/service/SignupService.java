package com.gavoza.backend.domain.user.all.service;

import com.gavoza.backend.domain.user.all.dto.request.SignupRequestDto;
import com.gavoza.backend.domain.user.all.entity.User;
import com.gavoza.backend.domain.user.all.repository.UserRepository;
import com.gavoza.backend.domain.user.all.validator.UserValidator;
import com.gavoza.backend.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public String signup(SignupRequestDto requestDto) throws IllegalArgumentException {
        String email = requestDto.getEmail();
        String nickname = requestDto.getNickname();
        String password = requestDto.getPassword();
        String hometown = requestDto.getHometown();
        String movedDate = requestDto.getMovedDate();
        String gender = requestDto.getGender();
        String birthDate = requestDto.getBirthDate();

        userValidator.validateEmail(requestDto.getEmail());
        userValidator.validateNickname(requestDto.getNickname());
        userValidator.validateHometown(requestDto.getHometown());
        userValidator.validateMovedDate(requestDto.getMovedDate());
        userValidator.validateGender(requestDto.getGender());
        userValidator.validateBirthDate(requestDto.getBirthDate());

        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(email, nickname, encodedPassword, hometown, movedDate, gender, birthDate);

        userRepository.save(user);

        return user.getEmail();
    }

    @Transactional
    public void delete(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        userRepository.delete(user);
    }
}

