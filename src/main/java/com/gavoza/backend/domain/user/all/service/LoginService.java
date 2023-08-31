package com.gavoza.backend.domain.user.all.service;

import com.gavoza.backend.domain.user.all.validator.UserValidator;
import com.gavoza.backend.domain.user.all.entity.RefreshToken;
import com.gavoza.backend.domain.user.all.entity.User;
import com.gavoza.backend.domain.user.all.repository.RefreshTokenRepository;
import com.gavoza.backend.domain.user.all.repository.UserRepository;
import com.gavoza.backend.global.exception.EmailNotFoundException;
import com.gavoza.backend.global.exception.PasswordNotMatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserValidator userValidator;

    public String login(String email, String password) throws EmailNotFoundException, PasswordNotMatchException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("이메일이 존재하지 않습니다."));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordNotMatchException("비밀번호가 일치하지 않습니다.");
        }

        return user.getEmail();
    }

    @Transactional
    public void logout(String email) {
        Optional<RefreshToken> existingRefreshToken = refreshTokenRepository.findByUserEmail(email);
        existingRefreshToken.ifPresent(refreshTokenRepository::delete);
    }





}
