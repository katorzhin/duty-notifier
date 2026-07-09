package com.notification.dutynotifier.service.notificationService;

import com.notification.dutynotifier.bot.DutyBot;
import com.notification.dutynotifier.dto.notificationRequest.NotificationRequest;
import com.notification.dutynotifier.entity.subscriber.Subscriber;
import com.notification.dutynotifier.service.subscriberService.SubscriberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SubscriberService subscriberService;
    private final DutyBot dutyBot;

    public void sendToAll(String message) {

        List<Subscriber> subscribers = subscriberService.findAll();

        for (Subscriber subscriber : subscribers) {
            dutyBot.sendMessage(subscriber.getChatId(), message);
        }
    }

    public void sendNotification(NotificationRequest request) {
        sendToAll(request.getMessage());
    }
}