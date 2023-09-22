package com.gavoza.backend.domain.auth.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverUserResponseDto {
    private String resultcode;
    private String message;
    private Response response;

    @Getter
    @Setter
    public static class Response {
        private String id;
        private String nickname;
        private String email;
    }
}

