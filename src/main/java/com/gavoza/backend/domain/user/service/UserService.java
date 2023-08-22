package com.gavoza.backend.domain.user.service;

import com.gavoza.backend.domain.user.dto.Signup1RequestDto;
import com.gavoza.backend.domain.user.dto.Signup2RequestDto;
import com.gavoza.backend.domain.user.dto.UserUpdateRequestDto;
import com.gavoza.backend.domain.user.dto.UserValidator;
import com.gavoza.backend.domain.user.entity.RefreshToken;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.domain.user.repository.RefreshTokenRepository;
import com.gavoza.backend.domain.user.repository.UserRepository;
import com.gavoza.backend.global.exception.EmailNotFoundException;
import com.gavoza.backend.global.exception.MessageResponseDto;
import com.gavoza.backend.global.exception.PasswordNotMatchException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserValidator userValidator;


    @Transactional
    public void signup1(Signup1RequestDto requestDto) throws IllegalArgumentException {
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
    }

    @Transactional
    public void signup2(HttpServletRequest request, Signup2RequestDto requestDto) throws IllegalArgumentException {
        String userEmail = request.getParameter("email");
        if (userEmail == null || userEmail.isEmpty()) {
            throw new IllegalArgumentException("이메일이 없습니다.");
        }

        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        userValidator.validateHometown(requestDto.getHometown());
        userValidator.validateMovedDate(requestDto.getMovedDate());
        userValidator.validateGender(requestDto.getGender());
        userValidator.validateBirthDate(requestDto.getBirthDate());

        userRepository.save(user);
    }


    @Transactional
    public RefreshToken createAndSaveRefreshToken(String userEmail) {
        // 이메일에 대한 기존 리프레시 토큰 조회
        Optional<RefreshToken> existingRefreshToken = refreshTokenRepository.findByUserEmail(userEmail);

        if (existingRefreshToken.isPresent()) {
            RefreshToken refreshToken = existingRefreshToken.get();
            // 리프레시 토큰 값 업데이트
            refreshToken.updateToken(UUID.randomUUID().toString());

            // 유효기간 설정
            LocalDateTime expiryDate = LocalDateTime.now().plusDays(1);
            // 리프레시 토큰 만료일 업데이트
            refreshToken.updateExpiryDate(expiryDate);

            // 변경된 리프레시 토큰 저장
            refreshTokenRepository.save(refreshToken);

            return refreshToken;
        } else {
            // 새로운 리프레시 토큰 생성
            String refreshTokenValue = UUID.randomUUID().toString();
            LocalDateTime expiryDate = LocalDateTime.now().plusHours(1); // 1시간 설정

            RefreshToken refreshToken = new RefreshToken(refreshTokenValue, userEmail, expiryDate);
            refreshTokenRepository.save(refreshToken);

            return refreshToken;
        }
    }


    public String validateRefreshTokenAndReturnEmail(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new IllegalArgumentException("토큰이 유효하지 않습니다."));

        // 날짜 형식 확인
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(refreshToken.getExpiryDate())) {
            refreshTokenRepository.delete(refreshToken); // 토큰 만료시 삭제
            throw new IllegalArgumentException("토큰이 만료되었습니다.");
        }

        return refreshToken.getUserEmail();
    }


    public String login(String email, String password) throws EmailNotFoundException, PasswordNotMatchException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("이메일이 존재하지 않습니다."));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordNotMatchException("비밀번호가 일치하지 않습니다.");
        }

        return user.getEmail();
    }





    @Transactional
    public void updateUserInfo(String email, UserUpdateRequestDto requestDto) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        // UserValidator 인스턴스 생성
        UserValidator userValidator = new UserValidator(userRepository);

        // 닉네임 유효성 검사
        if (requestDto.getNickname() != null) {
            userValidator.validateNickname(requestDto.getNickname());
            user.setNickname(requestDto.getNickname());
        }

        // 비밀번호 변경
        if (requestDto.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
            user.setPassword(encodedPassword);
        }

        // hometown 변경
        if (requestDto.getHometown() != null) {
            userValidator.validateHometown(requestDto.getHometown());
            user.setHometown(requestDto.getHometown());
        }

        // movedDate 변경
        if (requestDto.getMovedDate() != null) {
            userValidator.validateMovedDate(requestDto.getMovedDate());
            user.setMovedDate(requestDto.getMovedDate());
        }

        // 성별 변경
        if (requestDto.getGender() != null) {
            userValidator.validateGender(requestDto.getGender());
            user.setGender(requestDto.getGender());
        }

        // birthDate 변경
        if (requestDto.getBirthDate() != null) {
            userValidator.validateBirthDate(requestDto.getBirthDate());
            user.setBirthDate(requestDto.getBirthDate());
        }

        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        userRepository.delete(user);
    }

}

