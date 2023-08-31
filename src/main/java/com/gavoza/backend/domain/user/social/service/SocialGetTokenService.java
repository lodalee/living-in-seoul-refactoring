package com.gavoza.backend.domain.user.social.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SocialGetTokenService {

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


}
