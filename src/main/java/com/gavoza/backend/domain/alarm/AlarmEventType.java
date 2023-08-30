package com.gavoza.backend.domain.alarm;

import lombok.Getter;

@Getter
public enum AlarmEventType {
    NEW_COMMENT_ON_POST(AlarmType.COMMENT),
    NEW_RECOMMENT_ON_POST(AlarmType.COMMENT),

    NEW_LIKE_ON_POST(AlarmType.LIKE),
    NEW_LIKE_ON_COMMENT(AlarmType.LIKE),
    NEW_LIKE_ON_RECOMMENT(AlarmType.LIKE),

    NEW_POST_WITH_HASHTAG(AlarmType.HASHTAG)
    ;


    private final AlarmType alarmType;

    AlarmEventType (AlarmType alarmType){
        this.alarmType = alarmType;
    }
    public AlarmType getAlarmType(){
        return this.alarmType;
    }
}
