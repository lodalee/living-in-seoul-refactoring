package com.gavoza.backend.domain.alarm.controller;

import com.gavoza.backend.domain.alarm.dto.request.HashtagRequestDto;
import com.gavoza.backend.domain.alarm.dto.response.AlarmSubscriptionStatusDto;
import com.gavoza.backend.domain.alarm.dto.response.AlarmsResponseDto;
import com.gavoza.backend.domain.alarm.service.AlarmService;
import com.gavoza.backend.domain.alarm.type.AlarmType;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.dto.MessageResponseDto;
import com.gavoza.backend.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alarm")
@RequiredArgsConstructor
public class AlarmController {
    private final AlarmService alarmService;
    //활동 및 해시태그 알림 조회
    @GetMapping("/activity")
    public AlarmsResponseDto getNotification(@RequestParam int page,
                                             @RequestParam int size,
                                             @RequestParam String alarmCategory,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return alarmService.getNotification(page-1, size, alarmCategory,  user);
    }

    //알림 구독
    @PostMapping("/subscribe")
    public MessageResponseDto subscribeAlarm(@RequestBody AlarmType alarmType,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userId = userDetails.getUser().getId();
        return alarmService.subscribeAlarm(alarmType,userId);
    }

    //알림 구독 조회
    @GetMapping("/subscribe")
    public AlarmSubscriptionStatusDto getSubscribeAlarm(@AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return alarmService.getSubscribeAlarm(user);
    }

    //해시태그 구독
    @PostMapping("/hashtag")
    public MessageResponseDto subscribeHashtag(@RequestBody HashtagRequestDto requestDto,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userId = userDetails.getUser().getId();
        return alarmService.subscribeHashtag(requestDto, userId);
    }

    //해시태그 구독 취소
    @DeleteMapping("/hashtag")
    public MessageResponseDto deleteSubscribeHashtag(@RequestBody HashtagRequestDto requestDto,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userId = userDetails.getUser().getId();
        return alarmService.unsubscribeHashtag(requestDto, userId);
    }

    //구독된 해시태그 조회
    @GetMapping("/hashtag")
    public List<String> subscribeHashtagList(@AuthenticationPrincipal UserDetailsImpl userDetails){

        User user = userDetails.getUser();
        return alarmService.subscribeHashtagList(user);
    }

    //알림 눌렀을 때 is read true로
    @PostMapping("/read/{notificationId}")
    public MessageResponseDto markNotificationAsRead(@PathVariable Integer notificationId,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return alarmService.markNotificationAsRead(notificationId, user);
    }
}
