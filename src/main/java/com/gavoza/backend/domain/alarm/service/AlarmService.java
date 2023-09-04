package com.gavoza.backend.domain.alarm.service;

import com.gavoza.backend.domain.alarm.AlarmType;
import com.gavoza.backend.domain.alarm.entity.Alarm;
import com.gavoza.backend.domain.alarm.entity.SubscribeHashtag;
import com.gavoza.backend.domain.alarm.repository.AlarmRepository;
import com.gavoza.backend.domain.alarm.repository.SubscribeHashtagRepository;
import com.gavoza.backend.domain.alarm.response.AlarmListResponse;
import com.gavoza.backend.domain.alarm.response.AlarmResponse;
import com.gavoza.backend.domain.alarm.response.SubAlarmResponseDto;
import com.gavoza.backend.domain.user.all.entity.User;
import com.gavoza.backend.domain.user.all.repository.UserRepository;
import com.gavoza.backend.domain.user.all.dto.response.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final SubscribeHashtagRepository subscribeHashtagRepository;

    //알림 조회
    public AlarmListResponse getNotification(int page, int size, String alarmCategory, User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "registeredAt"));
        Page<Alarm> alarmPages = alarmRepository.findAllByUser(user, pageable);

        List<AlarmResponse> alarmResponses;

        if (alarmCategory.equals("activity")) {
            // activity일 때 필터링
            alarmResponses = alarmPages.stream()
                    .filter(alarm -> alarm.getAlarmEventType().getAlarmType().equals(AlarmType.LIKE)
                            || alarm.getAlarmEventType().getAlarmType().equals(AlarmType.COMMENT))
                    .map(alarm -> new AlarmResponse(
                            alarm.getId(),
                            alarm.getAlarmEventType(),
                            alarm.getNotificationMessage(),
                            alarm.getIsRead(),
                            alarm.getRegisteredAt(),
                            alarm.getUserImg()
                    ))
                    .collect(Collectors.toList());
        } else if (alarmCategory.equals("hashtag")) {
            // hashtag일 때 필터링
            alarmResponses = alarmPages.stream()
                    .filter(alarm -> alarm.getAlarmEventType().getAlarmType().equals(AlarmType.HASHTAG))
                    .map(alarm -> new AlarmResponse(
                            alarm.getId(),
                            alarm.getAlarmEventType(),
                            alarm.getNotificationMessage(),
                            alarm.getIsRead(),
                            alarm.getRegisteredAt(),
                            alarm.getUserImg(),
                            alarm.getHashtagName()
                    ))
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("알림 타입이 존재하지 않습니다.");
        }

        return new AlarmListResponse("조회 성공", alarmPages.getTotalPages(), alarmPages.getTotalElements(), size, alarmResponses);
    }


    //알림 구독
    @Transactional
    public MessageResponseDto subscribeAlarm(AlarmType alarmType, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        if (alarmType.equals(AlarmType.LIKE)) {
            user.changeLikeAlarm();
        } else if (alarmType.equals(AlarmType.COMMENT)) {
            user.changeCommentAlarm();
        } else if (alarmType.equals(AlarmType.HASHTAG)) {
            user.changeHashtagAlarm();
        }
        return new MessageResponseDto(alarmType + " 구독이 변경 완료되었습니다.");
    }

    //알림 구독 조회
    public SubAlarmResponseDto getSubscribeAlarm(User user) {
        return new SubAlarmResponseDto(user);
    }

    //해시 태그 구독
    public MessageResponseDto subscribeHashtag(String hashtag, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        SubscribeHashtag subscribeHashtag = new SubscribeHashtag(user, hashtag);
        subscribeHashtagRepository.save(subscribeHashtag);

        return new MessageResponseDto("해시태그 구독 완료");
    }

    // 해시태그 구독 취소
    public MessageResponseDto unsubscribeHashtag(String hashtag, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        // 사용자가 해당 해시태그를 구독한 레코드를 찾아서 삭제
        SubscribeHashtag subscribeHashtag = subscribeHashtagRepository.findByUserAndHashtag(user, hashtag);
        if (subscribeHashtag != null) {
            subscribeHashtagRepository.delete(subscribeHashtag);
            return new MessageResponseDto("해시태그 구독 취소 완료");
        } else {
            return new MessageResponseDto("사용자가 해당 해시태그를 구독하지 않았습니다.");
        }
    }

    //구독된 해시태그 조회
    public List<String> subscribeHashtagList(User user) {
        List<SubscribeHashtag> subscribeHashtagList = subscribeHashtagRepository.findAllByUser(user);

        List<String> hashtagNames = new ArrayList<>();
        for (SubscribeHashtag subscribeHashtag : subscribeHashtagList) {
            hashtagNames.add(subscribeHashtag.getHashtag());
        }

        return hashtagNames;
    }

    // 알림 클릭 후 알림 읽음 처리
    public MessageResponseDto markNotificationAsRead(@PathVariable Integer notificationId, User user) {
        // 알림 조회
        Alarm alarm = alarmRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 알림은 존재하지 않습니다."));

        // 사용자 인증 및 권한 검사
        if (!alarm.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("알림에 대한 액세스 권한이 없습니다.");
        }

        // 알림을 읽음 처리
        alarm.setIsRead(true);
        alarmRepository.save(alarm);

        return new MessageResponseDto("알림을 읽음 처리했습니다.");
    }


}





