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
import com.gavoza.backend.global.exception.PasswordNotMatchException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    private final UserValidator userValidator;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

//    @Value("${spring.security.oauth2.client.registration.google.client-id}")
//    private String googleClientId;
//
//    private final OAuth2AuthorizedClientService authorizedClientService;
//    private final ClientRegistrationRepository clientRegistrationRepository;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;



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

    @Transactional
    public void logout(String email) {
        Optional<RefreshToken> existingRefreshToken = refreshTokenRepository.findByUserEmail(email);
        existingRefreshToken.ifPresent(refreshTokenRepository::delete);
    }

    public String signInWithKakao(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<User> responseEntity = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                requestEntity,
                User.class
        );

        User kakaoUser = responseEntity.getBody();

        if (kakaoUser == null) {
            throw new IllegalArgumentException("카카오 유저 정보를 가져올 수 없습니다.");
        }

        // 추가: 이메일 확인
        String email = kakaoUser.getEmail();

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
//                .queryParam("client_secret", clientSecret)
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
                new ParameterizedTypeReference<>() {});

        if (responseEntity.getBody() != null && responseEntity.getBody().containsKey("access_token")) {
            return responseEntity.getBody().get("access_token").toString();
        } else {
            throw new IllegalStateException("액세스 토큰을 가져올 수 없습니다.");
        }
    }

    public String signInWithGoogle(OAuth2AuthenticationToken authentication) {
        OAuth2User userInfo = authentication.getPrincipal();

        // 구글 사용자 정보에서 이메일을 가져온다.
        String email = userInfo.getAttribute("email");

        // DB에서 해당 이메일에 대한 사용자 찾기
        Optional<User> existingUserOptional = userRepository.findByEmail(email);

        // 만약 DB에 해당 이메일의 사용자가 없다면 회원 가입 진행
        if (!existingUserOptional.isPresent()) {
            // 구글 사용자 이름을 닉네임으로 설정
            String nickname = userInfo.getAttribute("name");

            // 임시 비밀번호 생성 (랜덤 UUID)
            String password = UUID.randomUUID().toString();

            // 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(password);

            // 새로운 사용자 객체 생성
            User newUser = new User(email, nickname, encodedPassword);

            // DB에 저장
            userRepository.save(newUser);
        }

        return email;
    }
    public String getAccessTokenFromNaverAuthCode(String authCode) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("grant_type", "authorization_code");
        queryParams.add("client_id", naverClientId);
        queryParams.add("redirect_uri", naverRedirectUri);
        queryParams.add("code", authCode);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(queryParams, headers);

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {});

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
}
