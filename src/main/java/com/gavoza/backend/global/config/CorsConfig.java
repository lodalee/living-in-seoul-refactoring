package com.gavoza.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // 허용할 도메인 설정
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메소드 설정
                .allowedHeaders("*") // 요청 헤더 중 허용할 항목 설정
                .allowCredentials(true) // 인증 정보를 사용할 경우 true로 설정 (옵션)
                .maxAge(3600); // preflight 요청의 유효 시간(초) 설정 (옵션)
    }
}