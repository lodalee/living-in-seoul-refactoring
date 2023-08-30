package com.gavoza.backend.domain.alarm.response;

import com.gavoza.backend.domain.alarm.AlarmEventType;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class AlarmResponse {
    private Long id;
    private AlarmEventType alarmEventType;
    //알림의 내용
    private String text;
    private Boolean isRead;
    private LocalDateTime registeredAt;
    public AlarmResponse(Long id, AlarmEventType alarmEventType, String text, Boolean isRead, LocalDateTime registeredAt){
        this.id = id;
        this.alarmEventType = alarmEventType;
        this.text = text;
        this.isRead = isRead;
        this.registeredAt = registeredAt;
    }
}
