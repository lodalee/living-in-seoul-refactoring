package com.gavoza.backend.domain.alarm.controller;

import com.gavoza.backend.domain.alarm.AlarmType;
import com.gavoza.backend.domain.alarm.response.AlarmListResponse;
import com.gavoza.backend.domain.alarm.service.AlarmService;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.exception.MessageResponseDto;
import com.gavoza.backend.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alarm")
@RequiredArgsConstructor
public class AlarmController {
    private final AlarmService alarmService;
    //활동 및 해시태그 알림 조회
    @GetMapping("/activity")
    public AlarmListResponse getNotification(@RequestParam int page,
                                                  @RequestParam int size,
                                                  @RequestParam String alarmCategory,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return alarmService.getNotification(page-1, size, alarmCategory,  user);
    }

    //알림 구독
    @PostMapping("/subscribe")
    public MessageResponseDto subscribeAlarm(@RequestParam AlarmType alarmType,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userId = userDetails.getUser().getId();
        return alarmService.subscribeAlarm(alarmType,userId);
    }

    //해시태그 구독
    @PostMapping("/hashtag")
    public MessageResponseDto subscribeHashtag(@RequestParam String hashtag,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userId = userDetails.getUser().getId();
        return alarmService.subscribeHashtag(hashtag, userId);
    }

    //해시태그 구독 취소
    @DeleteMapping("/hashtag")
    public MessageResponseDto deleteSubscribeHashtag(@RequestParam String hashtag,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userId = userDetails.getUser().getId();
        return alarmService.unsubscribeHashtag(hashtag, userId);
    }
}
