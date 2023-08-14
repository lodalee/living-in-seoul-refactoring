package com.gavoza.backend.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gavoza.backend.domain.user.dto.LoginRequestDto;
import com.gavoza.backend.domain.user.entity.RefreshToken;
import com.gavoza.backend.domain.user.service.UserService;
import com.gavoza.backend.global.exception.TokenResMsgDto;
import com.gavoza.backend.global.jwt.JwtUtil;
import com.gavoza.backend.global.security.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        setFilterProcessesUrl("/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        log.info("로그인 시도");
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getEmail(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        log.info("로그인 성공 및 JWT 생성");
        String email = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getEmail();

        // 액세스 토큰 생성
        String accessToken = jwtUtil.createAccessToken(email);

        // 리프레시 토큰 생성 및 저장
        RefreshToken refreshToken = userService.createAndSaveRefreshToken(email);

        // 응답 바디에 액세스 토큰 및 리프레시 토큰 추가
        TokenResMsgDto responseBody = new TokenResMsgDto("로그인에 성공하셨습니다.", accessToken, refreshToken.getToken());
        response.setContentType("application/json; charset=UTF-8");
        try {
            response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
        } catch (IOException e) {
            log.error("응답 작성 중 문제 발생: {}", e.getMessage());
            throw new RuntimeException("응답 작성 중 문제 발생", e);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        log.info("로그인 실패");
        response.setStatus(401);
    }
}
