package com.gavoza.backend.domain.alarm.dto.response;

import com.gavoza.backend.domain.user.entity.User;
import lombok.Getter;

@Getter
public class SubAlarmResponseDto {
    private boolean likeAlarm;
    private boolean commentAlarm;
    private boolean hashtagAlarm;

    public SubAlarmResponseDto(User user) {
        this.likeAlarm = user.isLikeAlarm();
        this.commentAlarm = user.isCommentAlarm();
        this.hashtagAlarm = user.isHashtagAlarm();
    }
}
