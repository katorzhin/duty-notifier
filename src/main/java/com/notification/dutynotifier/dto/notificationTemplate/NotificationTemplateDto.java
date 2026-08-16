package com.notification.dutynotifier.dto.notificationTemplate;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationTemplateDto {

    @NotBlank
    private String todayTemplate;

    @NotBlank
    private String scheduleTemplate;
}