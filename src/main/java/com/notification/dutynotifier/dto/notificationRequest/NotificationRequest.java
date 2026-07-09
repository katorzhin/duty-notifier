package com.notification.dutynotifier.dto.notificationRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class NotificationRequest {

    @NotBlank
    private String message;

    private boolean sendToAll;

    private List<Long> employeeIds;
}