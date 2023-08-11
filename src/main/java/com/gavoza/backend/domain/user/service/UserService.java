package com.gavoza.backend.domain.user.service;

import com.gavoza.backend.domain.user.dto.LoginRequestDto;
import com.gavoza.backend.domain.user.dto.SignupRequestDto;
import com.gavoza.backend.domain.user.entity.Location;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.domain.user.repository.LocationRepository;
import com.gavoza.backend.domain.user.repository.UserRepository;
import com.gavoza.backend.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(SignupRequestDto requestDto) {
        String email = requestDto.getEmail();
        String nickname = requestDto.getNickname();
        String password = requestDto.getPassword();
        String gu = requestDto.getGu();
        String dong = requestDto.getDong();
        String hometown = requestDto.getHometown();
        String movedDate = requestDto.getMovedDate();

        // movedDate 유효성 검증
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            LocalDate.parse(movedDate, formatter); //객체로 파싱
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

        User user = new User(email, nickname, encodedPassword, hometown, guDong, movedDate);

        userRepository.save(user);
    }
}
