package com.gavoza.backend.domain.user.social.service;

import com.gavoza.backend.domain.user.all.entity.User;
import com.gavoza.backend.domain.user.all.repository.UserRepository;
import com.gavoza.backend.domain.user.social.dto.GoogleUserResponseDto;
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


    public User signInWithKakao(String accessToken) {
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
            newUser.setIsNew(true);  // 신규 가입 여부 표시

            return userRepository.save(newUser);  // DB에 저장 후 반환
        }

        return existingUserOptional.get();  // 이미 존재하는 유저 정보 반환
    }


    public User signInWithNaver(String accessToken) {
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
            newUser.setIsNew(true);  // 신규 가입 여부 표시

            return userRepository.save(newUser);  // DB에 저장 후 반환
        }

        return existingUserOptional.get();
    }

    public User signInWithGoogle(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<GoogleUserResponseDto> responseEntity = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v1/userinfo?alt=json",
                HttpMethod.GET,
                requestEntity,
                GoogleUserResponseDto.class);

        GoogleUserResponseDto googleUser = responseEntity.getBody();

        if (googleUser == null || googleUser.getEmail() == null) {
            throw new IllegalArgumentException("구글 유저 정보를 가져올 수 없습니다.");
        }

        // 이메일 확인
        String email = googleUser.getEmail();

        // 유저가 이미 존재하는지 체크하고, 없다면 새로 생성한다.
        Optional<User> existingUserOptional = userRepository.findByEmail(email);
        if (!existingUserOptional.isPresent()) {
            User newUser = createUser(googleUser);
            newUser.setIsNew(true);  	//신규 가입 여부 표시

            return userRepository.save(newUser);
        }

        return existingUserOptional.get();
    }


    private User createUser(GoogleUserResponseDto googleUserInfo) {
        // 임시 비밀번호 생성 (랜덤 UUID)
        String password = UUID.randomUUID().toString();
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 이름 설정 (null 체크)
        String name = googleUserInfo.getName() != null ?
                googleUserInfo.getName() : "소셜유저";

        String uniqueNickname = generateUniqueNickname(name);

        return new User(googleUserInfo.getEmail(), uniqueNickname, encodedPassword);
    }

    private String generateUniqueNickname(String baseName) {
        int count = 1;

        while(userRepository.existsByNickname(baseName + count)) {
            count++;
        }

        return baseName + count;
    }
}
