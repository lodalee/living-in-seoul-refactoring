package com.gavoza.backend.domain.user.service;

import com.gavoza.backend.domain.user.dto.SignupRequestDto;
import com.gavoza.backend.domain.user.entity.Location;
import com.gavoza.backend.domain.user.entity.RefreshToken;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.domain.user.repository.LocationRepository;
import com.gavoza.backend.domain.user.repository.RefreshTokenRepository;
import com.gavoza.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void signup(SignupRequestDto requestDto) {
        String email = requestDto.getEmail();
        String nickname = requestDto.getNickname();
        String password = requestDto.getPassword();
        String gu = requestDto.getGu();
        String dong = requestDto.getDong();
        String hometown = requestDto.getHometown();
        String movedDate = requestDto.getMovedDate();
        String gender = requestDto.getGender();

        // movedDate 유효성 검증
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            LocalDate.parse(movedDate, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("상경 날짜는 연도-월-일 형식이여야 합니다.");
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

        //데이터베이스에 있는 구 인지 확인
        List<Location> guList = locationRepository.findByGu(gu);
        if (guList.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 구입니다.");
        }

        //구가 있으면 동이 그 구에 해당하는지 확인
        Location guDong = locationRepository.findByGuAndDong(gu, dong)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 동입니다."));

        String encodedPassword = passwordEncoder.encode(password);

        if (!gender.equalsIgnoreCase("남자") && !gender.equalsIgnoreCase("여자")) {
            throw new IllegalArgumentException("성별은 남자 혹은 여자이여야합니다.");
        }

        User user = new User(email, nickname, encodedPassword, hometown, guDong, movedDate, gender);
        userRepository.save(user);
    }


    @Transactional
    public RefreshToken createAndSaveRefreshToken(String userEmail) {
        Optional<RefreshToken> existingRefreshToken = refreshTokenRepository.findByUserEmail(userEmail);

        if (existingRefreshToken.isPresent()) {
            RefreshToken refreshToken = existingRefreshToken.get();
            refreshToken.updateToken(UUID.randomUUID().toString());
            LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);
            refreshToken.updateExpiryDate(expiryDate);

            refreshTokenRepository.save(refreshToken);

            return refreshToken;
        } else {
            String refreshTokenValue = UUID.randomUUID().toString();
            LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);

            RefreshToken refreshToken = new RefreshToken(refreshTokenValue, userEmail, expiryDate);
            refreshTokenRepository.save(refreshToken);

            return refreshToken;
        }
    }


    public String validateRefreshTokenAndReturnEmail(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new IllegalArgumentException("토큰이 유효하지 않습니다."));
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(refreshToken.getExpiryDate())) {
            refreshTokenRepository.delete(refreshToken);
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

