package com.gavoza.backend.domain.user.all.service;

import com.gavoza.backend.domain.Like.repository.CommentLikeRepository;
import com.gavoza.backend.domain.Like.repository.PostLikeRepository;
import com.gavoza.backend.domain.Like.repository.ReCommentLikeRepository;
import com.gavoza.backend.domain.alarm.repository.AlarmRepository;
import com.gavoza.backend.domain.alarm.repository.SubscribeHashtagRepository;
import com.gavoza.backend.domain.comment.repository.CommentRepository;
import com.gavoza.backend.domain.comment.repository.ReCommentRepository;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.report.repository.ReportRepository;
import com.gavoza.backend.domain.scrap.repository.PostScrapRepository;
import com.gavoza.backend.domain.user.all.dto.request.SignupRequestDto;
import com.gavoza.backend.domain.user.all.entity.User;
import com.gavoza.backend.domain.user.all.repository.FavoriteLocationRepository;
import com.gavoza.backend.domain.user.all.repository.UserRepository;
import com.gavoza.backend.domain.user.all.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;

    private final AlarmRepository alarmRepository;
    private final SubscribeHashtagRepository subscribeHashtagRepository;
    private final CommentRepository commentRepository;
    private final ReCommentRepository reCommentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostLikeRepository postLikeRepository;
    private final ReCommentLikeRepository reCommentLikeRepository;
    private final PostRepository postRepository;
    private final ReportRepository reportRepository;
    private final PostScrapRepository postScrapRepository;
    private final FavoriteLocationRepository favoriteLocationRepository;


    @Transactional
    public String signup(SignupRequestDto requestDto) throws IllegalArgumentException {
        String email = requestDto.getEmail();
        String nickname = requestDto.getNickname();
        String password = requestDto.getPassword();
        String hometown = requestDto.getHometown();
        String movedDate = requestDto.getMovedDate();
        String gender = requestDto.getGender();
        String birthDate = requestDto.getBirthDate();

        userValidator.validateEmail(requestDto.getEmail());
        userValidator.validateNickname(requestDto.getNickname());
        userValidator.validateHometown(requestDto.getHometown());
        userValidator.validateMovedDate(requestDto.getMovedDate());
        userValidator.validateGender(requestDto.getGender());
        userValidator.validateBirthDate(requestDto.getBirthDate());

        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(email, nickname, encodedPassword, hometown, movedDate, gender, birthDate);

        userRepository.save(user);

        return user.getEmail();
    }

    @Transactional
    public void delete(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        commentLikeRepository.deleteByUserId(user.getId());
        alarmRepository.deleteByUserId(user.getId());
        favoriteLocationRepository.deleteByUserId(user.getId());
        subscribeHashtagRepository.deleteByUserId(user.getId());
        postScrapRepository.deleteByUserId(user.getId());
        reCommentLikeRepository.deleteByUserId(user.getId());
        reCommentRepository.deleteByUserId(user.getId());
        commentRepository.deleteByUserId(user.getId());
        postLikeRepository.deleteByUserId(user.getId());
        postRepository.deleteByUserId(user.getId());
        reportRepository.deleteByUserId(user.getId());

        userRepository.delete(user);
    }
}

