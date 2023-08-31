package com.gavoza.backend.domain.user.social.service;

import com.gavoza.backend.domain.user.all.entity.User;
import com.gavoza.backend.domain.user.all.repository.UserRepository;
import com.gavoza.backend.domain.user.social.dto.KakaoUserResponseDto;
import com.gavoza.backend.domain.user.social.dto.NaverUserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SocialSigninService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public String signInWithKakao(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserResponseDto> responseEntity = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                requestEntity,
                KakaoUserResponseDto.class
        );

        KakaoUserResponseDto kakaoUser = responseEntity.getBody();

        if (kakaoUser == null) {
            throw new IllegalArgumentException("카카오 유저 정보를 가져올 수 없습니다.");
        }

        // 추가: 이메일 확인
        String email = kakaoUser.getKakao_account().getEmail();

        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 카카오 유저입니다. 이메일 정보가 없습니다.");
        }

        // DB에서 사용자 찾기
        Optional<User> existingUserOptional = userRepository.findByEmail(email);

        if (!existingUserOptional.isPresent()) {  // 만약 DB에 해당 이메일의 사용자가 없다면 회원 가입 진행
            String nickname = String.valueOf(kakaoUser.getId());  // 카카오 ID를 닉네임으로 설정

            String password = UUID.randomUUID().toString();  // 임시 비밀번호 생성 (랜덤 UUID)
            String encodedPassword = passwordEncoder.encode(password);  // 비밀번호 암호화

            User newUser = new User(email, nickname, encodedPassword);
            userRepository.save(newUser);  // DB에 저장
        }

        return email;
    }


    public String signInWithNaver(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<NaverUserResponseDto> responseEntity = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                requestEntity,
                NaverUserResponseDto.class);

        NaverUserResponseDto naverUser = responseEntity.getBody();

        if (naverUser == null || !"00".equals(naverUser.getResultcode())) {
            throw new IllegalArgumentException("네이버 유저 정보를 가져올 수 없습니다.");
        }

        String email = naverUser.getResponse().getEmail();

        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 네이버 유저입니다. 이메일 정보가 없습니다.");
        }

        Optional<User> existingUserOptional = userRepository.findByEmail(email);

        if (!existingUserOptional.isPresent()) {  // 만약 DB에 해당 이메일의 사용자가 없다면 회원 가입 진행

            // 닉네임 설정 (null 체크)
            String nickname = naverUser.getResponse().getNickname() != null ?
                    naverUser.getResponse().getNickname() : "";

            // 임시 비밀번호 생성 (랜덤 UUID)
            String password = UUID.randomUUID().toString();

            // 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(password);

            User newUser = new User(email, nickname, encodedPassword);

            userRepository.save(newUser);  // DB에 저장

        }

        return email;
    }
}