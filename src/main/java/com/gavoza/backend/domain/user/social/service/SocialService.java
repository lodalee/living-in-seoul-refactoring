package com.gavoza.backend.domain.user.social.service;

import com.gavoza.backend.domain.user.social.dto.KakaoUserResponseDto;
import com.gavoza.backend.domain.user.all.entity.User;
import com.gavoza.backend.domain.user.all.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

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


    public String getAccessTokenFromAuthCode(String authCode) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        String grantType = "authorization_code";

        URI uri = UriComponentsBuilder.fromHttpUrl("https://kauth.kakao.com/oauth/token")
                .queryParam("grant_type", grantType)
                .queryParam("client_id", kakaoClientId)
                .queryParam("redirect_uri", kakaoRedirectUri)
                .queryParam("code", authCode)
                .build()
                .encode()
                .toUri();

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {
                });
        System.out.println(responseEntity.getBody());

        if (responseEntity.getBody() != null && responseEntity.getBody().containsKey("access_token")) {
            return responseEntity.getBody().get("access_token").toString();
        } else {
            throw new IllegalStateException("액세스 토큰을 가져올 수 없습니다.");
        }
    }

    public String getAccessTokenFromNaverAuthCode(String authCode) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("grant_type", "authorization_code");
        queryParams.add("client_id", naverClientId);
        queryParams.add("client_secret", naverClientSecret);
        queryParams.add("code", authCode);
        queryParams.add("state", "naver");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(queryParams, headers);

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {
                });
        System.out.println(responseEntity);
        if (responseEntity.getBody() != null && responseEntity.getBody().containsKey("access_token")) {
            return responseEntity.getBody().get("access_token").toString();
        } else {
            throw new IllegalStateException("액세스 토큰을 가져올 수 없습니다.");
        }
    }

    public String signInWithNaver(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<>() {
                });

        if (responseEntity.getBody() != null && responseEntity.getBody().containsKey("response")) {
            Map<String, Object> response = (Map<String, Object>) responseEntity.getBody().get("response");

            String email = (String) response.get("email");

            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("유효하지 않은 네이버 유저입니다. 이메일 정보가 없습니다.");
            }

            Optional<User> existingUserOptional = userRepository.findByEmail(email);

            if (!existingUserOptional.isPresent()) {
                String nickname = (String) response.get("name");
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                User newUser = new User(email, nickname, encodedPassword);
                userRepository.save(newUser);
            }

            return email;
        } else {
            throw new IllegalArgumentException("네이버 유저 정보를 가져올 수 없습니다.");
        }
    }

    public String getAccessTokenFromGoogleAuthCode(String authCode) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("grant_type", "authorization_code");
        queryParams.add("client_id", googleClientId);
        queryParams.add("client_secret", googleClientSecret);

        if (authCode == null || authCode.trim().isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 인증 코드입니다.");
        }

        queryParams.add("code", authCode);

        if (googleRedirectUri == null || googleRedirectUri.trim().isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 리디렉션 URI입니다.");
        }

        queryParams.add("redirect_uri", googleRedirectUri);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(queryParams, headers);

        ResponseEntity<Map<String, Object>> responseEntity;

        try {
            responseEntity = restTemplate.exchange(
                    "https://oauth2.googleapis.com/token",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<>() {
                    });

            if (responseEntity.getBody() != null && responseEntity.getBody().containsKey("access_token")) {
                return responseEntity.getBody().get("access_token").toString();
            } else {
                throw new IllegalStateException("액세스 토큰을 가져올 수 없습니다.");
            }

        } catch (HttpClientErrorException e) {
            System.out.println(e.getResponseBodyAsString()); // Google OAuth 서버에서 오류 메시지 출력.
            throw e;
        }
    }

    public String signInWithGoogle(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange(
                        "https://www.googleapis.com/oauth2/v1/userinfo?alt=json",
                        HttpMethod.GET,
                        requestEntity,
                        new ParameterizedTypeReference<>() {
                        }
                );

        Map<String, Object> userInfoMap = response.getBody();

        if (userInfoMap == null || !userInfoMap.containsKey("email")) {
            throw new IllegalArgumentException("유효하지 않은 구글 유저입니다. 이메일 정보가 없습니다.");
        }

        // 이메일 확인
        String email = (String) userInfoMap.getOrDefault("email", "");

        // DB에서 사용자 찾기
        Optional<User> existingUserOptional = userRepository.findByEmail(email);

        if (!existingUserOptional.isPresent()) {
            // 만약 DB에 해당 이메일의 사용자가 없다면 회원 가입 진행

            String nickname = (String) userInfoMap.getOrDefault("name", "");

            User newUser = new User(email, nickname, passwordEncoder.encode(UUID.randomUUID().toString()));
            userRepository.save(newUser);
        }

        return email;
    }

}
