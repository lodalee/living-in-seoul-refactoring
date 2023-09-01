package com.gavoza.backend.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gavoza.backend.domain.user.all.dto.request.LoginRequestDto;
import com.gavoza.backend.domain.user.all.entity.RefreshToken;
import com.gavoza.backend.domain.user.all.entity.User;
import com.gavoza.backend.domain.user.all.repository.UserRepository;
import com.gavoza.backend.domain.user.all.service.LoginService;
import com.gavoza.backend.domain.user.all.dto.response.TokenResMsgDto;
import com.gavoza.backend.domain.user.all.validator.TokenValidator;
import com.gavoza.backend.global.jwt.JwtUtil;
import com.gavoza.backend.global.security.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final TokenValidator tokenValidator;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, TokenValidator tokenValidator,UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.tokenValidator = tokenValidator;
        this.userRepository = userRepository;
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
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        String email = userDetails.getUser().getEmail();

        // 사용자 정보 가져오기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        // 액세스 토큰 생성
        String accessToken = jwtUtil.createAccessToken(email);

        // 리프레시 토큰 생성 및 저장
        RefreshToken refreshTokenEntity = tokenValidator.createAndSaveRefreshToken(email);

        Date expirationDate= jwtUtil.getExpirationDateFromToken(accessToken);  // 액세스토큰의 만료시간 추출

        TokenResMsgDto responseBody =
                new TokenResMsgDto(user.getNickname(), "로그인에 성공하셨습니다.", accessToken,
                        refreshTokenEntity.getToken(), expirationDate);  // 응답 바디에 만료시간 추가

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
