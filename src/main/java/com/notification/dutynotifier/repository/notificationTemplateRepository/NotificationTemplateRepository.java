package com.notification.dutynotifier.repository.notificationTemplateRepository;

import com.notification.dutynotifier.entity.notificationTemplate.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTemplateRepository
        extends JpaRepository<NotificationTemplate, Long> {
}