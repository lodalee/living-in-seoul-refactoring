package com.gavoza.backend.domain.user.dto;

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

    @NotBlank(message = "성별을 입력해주세요.")
    private String gender;

    @NotBlank(message = "현재 거주중인 구를 입력해주세요.")
    private String gu;

    @NotBlank(message = "현재 거주중인 동을 입력해주세요.")
    private String dong;

    @NotBlank(message = "출신 지역을 입력해주세요.")
    private String hometown;

    @NotBlank(message = "상경 날짜를 입력해주세요.")
    private String movedDate;
}


