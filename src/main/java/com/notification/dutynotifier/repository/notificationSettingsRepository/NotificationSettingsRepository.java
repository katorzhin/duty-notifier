package com.notification.dutynotifier.repository.notificationSettingsRepository;

import com.notification.dutynotifier.entity.notificationSettings.NotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {
}