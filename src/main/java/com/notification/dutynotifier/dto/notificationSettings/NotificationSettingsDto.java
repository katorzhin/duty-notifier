package com.notification.dutynotifier.dto.notificationSettings;

import com.notification.dutynotifier.entity.notificationSettings.NotificationFrequency;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class NotificationSettingsDto {

    private boolean enabled;

    @NotNull
    private NotificationFrequency frequency;

    @NotNull
    private LocalTime firstNotificationTime;

    private LocalTime secondNotificationTime;
}
