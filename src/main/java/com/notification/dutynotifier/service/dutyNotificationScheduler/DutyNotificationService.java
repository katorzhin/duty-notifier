package com.notification.dutynotifier.service.dutyNotificationScheduler;

import com.notification.dutynotifier.entity.auditLog.AuditAction;
import com.notification.dutynotifier.entity.auditLog.messages.SystemAuditMessages;
import com.notification.dutynotifier.entity.notificationTemplate.NotificationTemplate;
import com.notification.dutynotifier.service.auditLogService.AuditLogService;
import com.notification.dutynotifier.service.dutyMessageService.DutyMessageService;
import com.notification.dutynotifier.service.notificationService.NotificationService;
import com.notification.dutynotifier.service.notificationTemplateService.NotificationTemplateService;
import com.notification.dutynotifier.service.subscriberService.SubscriberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DutyNotificationService {

    private final DutyMessageService dutyMessageService;
    private final SubscriberService subscriberService;
    private final NotificationService notificationService;
    private final NotificationTemplateService notificationTemplateService;
    private final AuditLogService auditLogService;

    public void sendDutyNotification() {
        System.out.println("SEND NOTIFICATION");
        NotificationTemplate template = notificationTemplateService.getTemplate();

        String today = dutyMessageService.buildTodayMessage(
                template.getTodayTemplate());

        String schedule = dutyMessageService.buildScheduleMessage(
                template.getScheduleTemplate());

        notificationService.sendToAll(today + "\n" + schedule);

        auditLogService.log(
                "SYSTEM",
                AuditAction.NOTIFICATION_SENT,
                SystemAuditMessages.scheduled(subscriberService.count())
        );
    }
}