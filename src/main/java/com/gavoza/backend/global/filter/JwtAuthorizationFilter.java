package com.gavoza.backend.global.filter;


import com.gavoza.backend.global.jwt.JwtUtil;
import com.gavoza.backend.global.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req, @NonNull HttpServletResponse res, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String tokenValue = jwtUtil.getTokenFromRequest(req);

        if (StringUtils.hasText(tokenValue)) {
            tokenValue = jwtUtil.substringToken(tokenValue);
            log.info(tokenValue);

            if (!jwtUtil.validateToken(tokenValue)) {
                log.error("Token Error");
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰");
                return;
            }

            Claims info = jwtUtil.getUserInfoFromToken(tokenValue);

            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error(e.getMessage());
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증에 실패했습니다.");
                return;
            }
        }

        filterChain.doFilter(req, res);
    }



    // 인증 처리
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }
}
