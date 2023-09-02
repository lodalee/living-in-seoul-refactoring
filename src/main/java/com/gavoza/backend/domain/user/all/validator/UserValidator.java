package com.gavoza.backend.domain.user.all.validator;

import com.gavoza.backend.domain.user.all.scrap.CityScraper;
import com.gavoza.backend.domain.user.all.entity.User;
import com.gavoza.backend.domain.user.all.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserValidator {
    private final UserRepository userRepository;

    private final CityScraper cityScraper = new CityScraper();
    private final Set<String> VALID_HOMETOWNS = new HashSet<>(cityScraper.getCities());

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateEmail(String email) {
        //이메일 중복확인
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    }

    public void validateNickname(String nickname) {
        // 닉네임 유효성 검사 코드
        Optional<User> checkNickname = userRepository.findByNickname(nickname);
        if (checkNickname.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
    }

    public void validateHometown(String hometown) {
        // 지역 유효성 검사 코드
        if (hometown != null && !VALID_HOMETOWNS.contains(hometown)) {
            throw new IllegalArgumentException("허용되지 않는 지역입니다.");
        }
    }

    public void validateMovedDate(String movedDate) {
        // 이주 날짜 유효성 검사 코드
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
    }

    public void validateGender(String gender) {
        // 성별 유효성 검사 코드
        if (gender != null && !("여성".equals(gender) || "남성".equals(gender))) {
            throw new IllegalArgumentException("성별은 '여성' 또는 '남성'만 입력 가능합니다.");
        }
    }

    public void validateBirthDate(String birthDate) {
        // 생년월일 유효성 검사 코드
        if (birthDate != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
                LocalDate.parse(birthDate, formatter); //객체로 파싱
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("생년월일은 연도-월-일 형식이여야 합니다.");
            }
        }
    }
}

