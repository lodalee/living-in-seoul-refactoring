package com.gavoza.backend.domain.alarm.repository;

import com.gavoza.backend.domain.alarm.entity.SubscribeHashtag;
import com.gavoza.backend.domain.user.all.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscribeHashtagRepository extends JpaRepository<SubscribeHashtag, Integer> {
    SubscribeHashtag findByUserAndHashtag(User user, String hashtag);

    List<SubscribeHashtag> findAllByUser(User user);
}
