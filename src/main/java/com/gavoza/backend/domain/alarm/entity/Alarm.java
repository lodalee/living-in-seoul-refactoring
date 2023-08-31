package com.gavoza.backend.domain.alarm.entity;

import com.gavoza.backend.domain.alarm.AlarmEventType;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.user.all.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "notification")
@NoArgsConstructor
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    // 알람을 받은 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 알람이 발생한 post
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Enumerated(EnumType.STRING)
    private AlarmEventType alarmEventType;

    private String notificationMessage;

    @Column(nullable = false)
    private Boolean isRead;

    private LocalDateTime registeredAt;

    private String hashtagName;
    private String userImg;

    public Alarm(Post post, User user, AlarmEventType alarmEventType, Boolean isRead, String notificationMessage, LocalDateTime registeredAt, String userImg) {
        this.post = post;
        this.user = user;
        this.alarmEventType = alarmEventType;
        this.notificationMessage = notificationMessage;
        this.isRead = isRead;
        this.registeredAt = registeredAt;
        this.userImg = userImg;
    }
    public Alarm(Post post, User user, AlarmEventType alarmEventType, Boolean isRead, String notificationMessage, LocalDateTime registeredAt, String userImg,String hashtagName) {
        this.post = post;
        this.user = user;
        this.alarmEventType = alarmEventType;
        this.notificationMessage = notificationMessage;
        this.isRead = isRead;
        this.registeredAt = registeredAt;
        this.hashtagName = hashtagName;
        this.userImg = userImg;
    }
}
