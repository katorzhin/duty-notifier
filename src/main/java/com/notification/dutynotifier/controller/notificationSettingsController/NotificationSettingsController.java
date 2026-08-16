package com.notification.dutynotifier.controller.notificationSettingsController;

import com.notification.dutynotifier.dto.notificationSettings.NotificationSettingsDto;
import com.notification.dutynotifier.entity.notificationSettings.NotificationSettings;
import com.notification.dutynotifier.mapper.NotificationSettingsMapper;
import com.notification.dutynotifier.service.notificationSchedulerService.NotificationSchedulerService;
import com.notification.dutynotifier.service.notificationSettingsService.NotificationSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification-settings")
@RequiredArgsConstructor
public class NotificationSettingsController {

    private final NotificationSettingsService service;
    private final NotificationSettingsMapper mapper;
    private final NotificationSchedulerService notificationSchedulerService;

    @GetMapping
    public NotificationSettingsDto getSettings() {
        return mapper.toResponse(service.getSettings());
    }

    @PutMapping
    public NotificationSettingsDto updateSettings(@RequestBody @Valid NotificationSettingsDto request) {

        NotificationSettings settings = service.updateSettings(mapper.toEntity(request));

        notificationSchedulerService.reschedule(settings);

        return mapper.toResponse(settings);
    }
}