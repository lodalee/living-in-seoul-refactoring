package com.gavoza.backend.domain.user.all.service;

import com.gavoza.backend.domain.user.all.dto.request.UserEditRequestDto;
import com.gavoza.backend.domain.user.all.entity.User;
import com.gavoza.backend.domain.user.all.repository.UserRepository;
import com.gavoza.backend.domain.user.all.validator.UserValidator;
import com.gavoza.backend.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProfileEditService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageEditService s3Service;


    public User findUserProfileByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));


    }

    @Transactional
    public void updateUserInfo(String email, UserEditRequestDto requestDto) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
       
        UserValidator userValidator = new UserValidator(userRepository);

        // 닉네임 유효성 검사
        if (requestDto.getNickname() != null) {
            userValidator.validateNickname(requestDto.getNickname());
            user.setNickname(requestDto.getNickname());
        }

        // 비밀번호 변경
        if (requestDto.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
            user.setPassword(encodedPassword);
        }

        // hometown 변경
        if (requestDto.getHometown() != null) {
            userValidator.validateHometown(requestDto.getHometown());
            user.setHometown(requestDto.getHometown());
        }

        // movedDate 변경
        if (requestDto.getMovedDate() != null) {
            userValidator.validateMovedDate(requestDto.getMovedDate());
            user.setMovedDate(requestDto.getMovedDate());
        }

        // 성별 변경
        if (requestDto.getGender() != null) {
            userValidator.validateGender(requestDto.getGender());
            user.setGender(requestDto.getGender());
        }

        // birthDate 변경
        if (requestDto.getBirthDate() != null) {
            userValidator.validateBirthDate(requestDto.getBirthDate());
            user.setBirthDate(requestDto.getBirthDate());
        }

        userRepository.save(user);
    }

    @Transactional
    public void updateProfileImage(String email, MultipartFile imageFile) throws IOException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("이메일 주소 " + email + "에 해당하는 사용자를 찾을 수 없습니다."));

        if (user.getProfileImageUrl() != null) {
            s3Service.deleteFile(user.getProfileImageUrl());
        }

        String imageUrl = s3Service.uploadFile(imageFile, "user");

        user.setProfileImageUrl(imageUrl);

        userRepository.save(user);
    }


    @Transactional
    public void deleteProfileImage(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("이메일 주소 " + email + "에 해당하는 사용자를 찾을 수 없습니다."));

        if (user.getProfileImageUrl() != null) {
            s3Service.deleteFile(user.getProfileImageUrl());
            user.setProfileImageUrl(null);
            userRepository.save(user);
        }
    }


}
