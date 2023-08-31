package com.gavoza.backend.domain.user.all.controller;

import com.gavoza.backend.domain.user.all.dto.response.MessageResponseDto;
import com.gavoza.backend.domain.user.all.dto.response.UserProfileResponseDto;
import com.gavoza.backend.domain.user.all.entity.User;
import com.gavoza.backend.domain.user.all.service.ProfileService;
import com.gavoza.backend.global.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final JwtUtil jwtUtil;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getMyProfile(HttpServletRequest request) {
        String email = jwtUtil.getEmailFromAuthHeader(request);

        User user = profileService.findUserProfileByEmail(email);

        UserProfileResponseDto userProfile = new UserProfileResponseDto(
                user.getNickname(),
                user.getBirthDate(),
                user.getMovedDate(),
                user.getGender(),
                user.getHometown(),
                user.getProfileImageUrl()
        );

        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/update")
    public ResponseEntity<MessageResponseDto> updateProfileImage(@RequestParam("image") MultipartFile imageFile,
                                                                 HttpServletRequest request) throws IOException {
        String email = jwtUtil.getEmailFromAuthHeader(request);
        profileService.updateProfileImage(email, imageFile);
        return ResponseEntity.ok(new MessageResponseDto("프로필 이미지가 수정되었습니다."));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<MessageResponseDto> deleteProfileImage(HttpServletRequest request) {
        String email = jwtUtil.getEmailFromAuthHeader(request);
        profileService.deleteProfileImage(email);
        return ResponseEntity.ok(new MessageResponseDto("프로필 이미지가 삭제되었습니다."));
    }

}