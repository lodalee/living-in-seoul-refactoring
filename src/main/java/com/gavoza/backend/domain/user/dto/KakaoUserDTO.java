package com.gavoza.backend.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoUserDTO {
    private Long id;
    private Properties properties;
    private KakaoAccount kakao_account;
    private String email;

// getters and setters...
@Getter
@Setter
    public static class Properties {
        private String nickname;
        private String profile_image;

// getters and setters...
    }
    @Getter
    @Setter
    public static class KakaoAccount {
        private String email;
        private Profile profile;

// getters and setters...
@Getter
@Setter
        public static class Profile {
            private String nickname;
            private String profile_image_url;

// getters and setters...
        }
    }
}

