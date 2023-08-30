package com.gavoza.backend.domain.alarm.service;

import com.gavoza.backend.domain.alarm.AlarmType;
import com.gavoza.backend.domain.alarm.entity.Alarm;
import com.gavoza.backend.domain.alarm.entity.SubscribeHashtag;
import com.gavoza.backend.domain.alarm.repository.AlarmRepository;
import com.gavoza.backend.domain.alarm.repository.SubscribeHashtagRepository;
import com.gavoza.backend.domain.alarm.response.AlarmListResponse;
import com.gavoza.backend.domain.alarm.response.AlarmResponse;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.domain.user.repository.UserRepository;
import com.gavoza.backend.global.exception.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

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

        if (alarmCategory.equals("activity")) {
            alarmPages = new PageImpl<>(alarmPages.stream()
                    .filter(alarm -> alarm.getAlarmEventType().getAlarmType().equals(AlarmType.LIKE)
                            || alarm.getAlarmEventType().getAlarmType().equals(AlarmType.COMMENT)).toList());

        } else if (alarmCategory.equals("hashtag")) {
            alarmPages = new PageImpl<>(alarmPages.stream()
                    .filter(alarm -> alarm.getAlarmEventType().getAlarmType().equals(AlarmType.HASHTAG)).toList());
        } else {
            throw new IllegalArgumentException("알림 타입이 존재하지 않습니다.");
        }

        // 조회 결과를 AlarmResponse 리스트로 변환
        List<AlarmResponse> alarmResponses = alarmPages.stream()
                .map(alarm -> new AlarmResponse(
                        alarm.getId(),
                        alarm.getAlarmEventType(),
                        alarm.getNotificationMessage(), // 알림 내용을 원하는 방식으로 생성
                        alarm.getIsRead(),
                        alarm.getRegisteredAt()
                ))
                .collect(Collectors.toList());
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





