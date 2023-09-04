package com.gavoza.backend.domain.alarm.repository;

import com.gavoza.backend.domain.alarm.entity.Alarm;
import com.gavoza.backend.domain.user.all.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Integer> {
    Page<Alarm> findAllByUser(User user, Pageable pageable);


    void deleteByUserId(Long id);
}
