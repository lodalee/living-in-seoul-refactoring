package com.gavoza.backend.domain.user.dto;

import com.gavoza.backend.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@NoArgsConstructor
public class LoginRequestDto {
    private String email;
    private String password;
    }



