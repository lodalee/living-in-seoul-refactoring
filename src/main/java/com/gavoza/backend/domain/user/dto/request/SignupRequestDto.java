package com.gavoza.backend.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class SignupRequestDto {

    @Email(message = "유효하지 않은 이메일 형식입니다.")
    @NotBlank(message = "이메일을 입력 해주세요")
    private String email;

    @NotBlank(message = "이름을 입력해주세요")
    private String nickname;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    private String hometown;
    private String gender;
    private String movedDate;
    private String birthDate;
}
