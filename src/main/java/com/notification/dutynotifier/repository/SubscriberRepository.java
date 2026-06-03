package com.notification.dutynotifier.repository;

import com.notification.dutynotifier.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    Optional<Subscriber> findByChatId(Long chatId);
}