package com.notification.dutynotifier.service.notificationSchedulerService;

import com.notification.dutynotifier.entity.notificationSettings.NotificationFrequency;
import com.notification.dutynotifier.entity.notificationSettings.NotificationSettings;
import com.notification.dutynotifier.service.dutyNotificationScheduler.DutyNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class NotificationSchedulerService {

    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Kyiv");

    private final ThreadPoolTaskScheduler taskScheduler;
    private final DutyNotificationService dutyNotificationService;
    private ScheduledFuture<?> firstTask;
    private ScheduledFuture<?> secondTask;

    public void reschedule(NotificationSettings settings) {
        System.out.println("Reschedule");
        cancelTasks();

        if (!settings.isEnabled()) {
            return;
        }

        scheduleFirst(settings);
        System.out.println("First notification scheduled: " + settings.getFirstNotificationTime());
        if (settings.getFrequency() == NotificationFrequency.TWICE) {
            scheduleSecond(settings);
            System.out.println("Second notification scheduled: " + settings.getSecondNotificationTime());
        }
    }

    private void scheduleFirst(NotificationSettings settings) {
        System.out.println("First cron: " + settings.getFirstNotificationTime());
        String cron = String.format(
                "0 %d %d * * *",
                settings.getFirstNotificationTime().getMinute(),
                settings.getFirstNotificationTime().getHour()
        );

        firstTask = taskScheduler.schedule(
                dutyNotificationService::sendDutyNotification,
                new CronTrigger(cron, ZONE_ID)
        );
    }

    private void scheduleSecond(NotificationSettings settings) {
        System.out.println("second cron: " + settings.getSecondNotificationTime());

        String cron = String.format(
                "0 %d %d * * *",
                settings.getSecondNotificationTime().getMinute(),
                settings.getSecondNotificationTime().getHour()
        );

        secondTask = taskScheduler.schedule(
                dutyNotificationService::sendDutyNotification,
                new CronTrigger(cron, ZONE_ID)
        );
    }

    private void cancelTasks() {

        if (firstTask != null) {
            firstTask.cancel(false);
        }

        if (secondTask != null) {
            secondTask.cancel(false);
        }
    }
}