package com.gavoza.backend.domain.alarm.service;

import com.gavoza.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class NotificationService {

    public static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    public void notifyAddEvent(User user, boolean check) {
        Long userId = user.getId();

        if (sseEmitters.containsKey(userId)) {
            if(check){
                SseEmitter sseEmitter = sseEmitters.get(userId);
                try {
                    sseEmitter.send(SseEmitter.event().name("addNotification").data("새로운 알림"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else{
            System.out.println("해당 유저가 연결되어 있지 않습니다.");
        }
    }

    public SseEmitter addSeeEmitter(Long userId){

        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        try {
            // 연결!!
            sseEmitter.send(SseEmitter.event().name("연결되었습니다."));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // user의 pk값을 key값으로 해서 SseEmitter를 저장
        sseEmitters.put(userId, sseEmitter);

        sseEmitter.onCompletion(() -> sseEmitters.remove(userId));
        sseEmitter.onTimeout(() -> sseEmitters.remove(userId));
        sseEmitter.onError((e) -> sseEmitters.remove(userId));
        return sseEmitter;
    }
}
