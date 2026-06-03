package com.notification.dutynotifier.service;

import com.notification.dutynotifier.bot.DutyBot;
import com.notification.dutynotifier.entity.Subscriber;
import com.notification.dutynotifier.repository.SubscriberRepository;
import com.notification.dutynotifier.service.dutyService.DutyService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DutyNotificationScheduler {

    private final DutyBot dutyBot;
    private final DutyService dutyService;
    private final SubscriberRepository subscriberRepository;

    @Scheduled(cron = "0 0 11,18 * * *")
    public void sendDutyNotification() {

        for (Subscriber subscriber : subscriberRepository.findAll()) {
            dutyBot.sendMessage(subscriber.getChatId(), dutyService.buildMessage());
        }
    }
}