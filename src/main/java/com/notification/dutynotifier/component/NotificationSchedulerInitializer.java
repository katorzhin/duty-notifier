package com.notification.dutynotifier.component;

import com.notification.dutynotifier.service.notificationSchedulerService.NotificationSchedulerService;
import com.notification.dutynotifier.service.notificationSettingsService.NotificationSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationSchedulerInitializer {

    private final NotificationSettingsService notificationSettingsService;
    private final NotificationSchedulerService notificationSchedulerService;

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        System.out.println("NotificationSchedulerInitializer");
        notificationSchedulerService.reschedule(
                notificationSettingsService.getSettings()
        );
    }
}