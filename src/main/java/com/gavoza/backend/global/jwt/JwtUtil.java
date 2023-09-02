package com.gavoza.backend.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    private final long TOKEN_TIME = 2 * 60 * 60 * 1000L;


    @Value("${jwt.secret.key}")
    private void setSecretKey(String secretKey) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    //토큰생성
    public String createToken(String email) {
        Date date = new Date();

        return Jwts.builder()
                .setSubject(email) //식별자값(ID)
                .setExpiration(new Date(date.getTime() + TOKEN_TIME)) //만료시간
                .setIssuedAt(date) //발급일
                .signWith(key, signatureAlgorithm) //암호화 알고리즘
                .compact();
    }


    //토큰 substring
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        throw new JwtException("토큰을 찾을 수 없습니다.");
    }

    //토큰 validate
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            throw new JwtException("유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            throw new JwtException("만료된 JWT 토큰 입니다.");
        } catch (UnsupportedJwtException e) {
            throw new JwtException("지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            throw new JwtException("잘못된 JWT 토큰 입니다.");
        }
    }


    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    //헤더에서 토큰 가져오기
    public String getTokenFromRequest(HttpServletRequest req) {
        String tokenValue = req.getHeader(AUTHORIZATION_HEADER);
        if (tokenValue != null && !tokenValue.isEmpty()) {
            return URLDecoder.decode(tokenValue, StandardCharsets.UTF_8);
        }
        return null;
    }

    //토큰생성
    public String createAccessToken(String email) {
        return createToken(email);

    }

    public String getEmailFromAuthHeader(HttpServletRequest request) {
        String tokenValue = request.getHeader(AUTHORIZATION_HEADER);

        if (tokenValue != null && tokenValue.startsWith(BEARER_PREFIX)) {
            String jwtToken = substringToken(tokenValue); // 토큰 값을 가져옵니다.
            if (validateToken(jwtToken)) { // 토큰이 유효한지 확인
                Claims claims = getUserInfoFromToken(jwtToken);
                return claims.getSubject(); // subject를 이메일로 저장한 경우 이메일을 반환합니다.
            } else {
                throw new JwtException("유효하지 않는 JWT 토큰 입니다.");
            }
        } else {
            throw new IllegalArgumentException("Authorization 헤더를 찾을 수 없거나 올바른 형식이 아닙니다.");
        }
    }


    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getExpiration();
    }


}
