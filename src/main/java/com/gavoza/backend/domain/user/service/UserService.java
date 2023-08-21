package com.gavoza.backend.domain.user.service;

import com.gavoza.backend.domain.user.dto.SignupRequestDto;
import com.gavoza.backend.domain.user.dto.UpdateUserRequestDto;
import com.gavoza.backend.domain.user.entity.CityScraper;
import com.gavoza.backend.domain.user.entity.RefreshToken;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.domain.user.repository.RefreshTokenRepository;
import com.gavoza.backend.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    private final CityScraper cityScraper = new CityScraper();
    private final Set<String> VALID_HOMETOWNS = new HashSet<>(cityScraper.getCities());

    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public void signup(SignupRequestDto requestDto) throws IllegalArgumentException {
        String email = requestDto.getEmail();
        String nickname = requestDto.getNickname();
        String password = requestDto.getPassword();
        String hometown = requestDto.getHometown();
        String movedDate = requestDto.getMovedDate();
        String gender = requestDto.getGender();
        String birthDate = requestDto.getBirthDate();

        // hometown 유효성 검증
        if (hometown != null && !VALID_HOMETOWNS.contains(hometown)) {
            throw new IllegalArgumentException("허용되지 않는 지역입니다.");
        }


       // movedDate 유효성 검증
        if (movedDate != null) {
            switch (movedDate) {
                case "~6개월":
                case "1~2년":
                case "3~4년":
                case "5년 이상":
                    break;
                default:
                    throw new IllegalArgumentException("상경 날짜 형식이 올바르지 않습니다.");
            }
        }

        //회원 중복확인
        Optional<User> checkNickname = userRepository.findByNickname(nickname);
        if (checkNickname.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        //이메일 중복확인
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // birthDate 유효성 검증
        if (birthDate != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
                LocalDate.parse(birthDate, formatter); //객체로 파싱
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("생년월일은 연도-월-일 형식이여야 합니다.");
            }
        }

        // 성별 유효성 검증
        if (gender != null && !("여성".equals(gender) || "남성".equals(gender))) {
            throw new IllegalArgumentException("성별은 '여성' 또는 '남성'만 입력 가능합니다.");
        }

        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(email, nickname, encodedPassword, hometown, movedDate, gender, birthDate);

        userRepository.save(user);
    }

    @Transactional
    public void updateUser(HttpServletRequest request, UpdateUserRequestDto requestDto) throws IllegalArgumentException {
        String userEmail = request.getParameter("email");
        if (userEmail == null || userEmail.isEmpty()) {
            throw new IllegalArgumentException("이메일이 없습니다.");
        }

        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // hometown 유효성 검사
        if (requestDto.getHometown() != null && !requestDto.getHometown().isEmpty()) {
            if (!VALID_HOMETOWNS.contains(requestDto.getHometown())) {
                throw new IllegalArgumentException("허용되지 않는 지역입니다.");
            }
            user.setHometown(requestDto.getHometown());
        }

        // movedDate 유효성 검사
        if (requestDto.getMovedDate() != null && !requestDto.getMovedDate().isEmpty()) {
            switch (requestDto.getMovedDate()) {
                case "~6개월":
                case "1~2년":
                case "3~4년":
                case "5년 이상":
                    user.setMovedDate(requestDto.getMovedDate());
                    break;
                default:
                    throw new IllegalArgumentException("이주 날짜 형식이 올바르지 않습니다.");
            }
        }

        // 성별 유효성 검사
        if (requestDto.getGender() != null && !requestDto.getGender().isEmpty()) {
            if (!("여성".equals(requestDto.getGender()) || "남성".equals(requestDto.getGender()))) {
                throw new IllegalArgumentException("성별은 '여성' 또는 '남성'만 입력 가능합니다.");
            }
            user.setGender(requestDto.getGender());
        }

        // birthDate 유효성 검사
        if (requestDto.getBirthDate() != null && !requestDto.getBirthDate().isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
                LocalDate.parse(requestDto.getBirthDate(), formatter); //객체로 파싱
                user.setBirthDate(requestDto.getBirthDate());
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("생년월일은 연도-월-일 형식이여야 합니다.");
            }
        }

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


    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일이 존재하지 않습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user.getEmail();
    }




}

