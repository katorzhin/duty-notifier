package com.notification.dutynotifier.service.notificationSettingsService;

import com.notification.dutynotifier.entity.notificationSettings.NotificationFrequency;
import com.notification.dutynotifier.entity.notificationSettings.NotificationSettings;
import com.notification.dutynotifier.repository.notificationSettingsRepository.NotificationSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class NotificationSettingsService {

    private static final Long SETTINGS_ID = 1L;

    private final NotificationSettingsRepository repository;

    public NotificationSettings getSettings() {
        return repository.findById(SETTINGS_ID)
                .orElseGet(this::createDefaultSettings);
    }

    public NotificationSettings updateSettings(NotificationSettings settings) {

        settings.setId(SETTINGS_ID);
        validateNotificationSettings(settings);
        return repository.save(settings);
    }

    private NotificationSettings createDefaultSettings() {

        NotificationSettings settings = NotificationSettings.builder()
                .id(SETTINGS_ID)
                .enabled(false)
                .frequency(NotificationFrequency.TWICE)
                .firstNotificationTime(LocalTime.of(11, 0))
                .secondNotificationTime(LocalTime.of(18, 0))
                .build();

        return updateSettings(settings);
    }

    private void validateNotificationSettings(NotificationSettings settings) {

        if (!settings.isEnabled()) {
            return;
        }

        if (settings.getFirstNotificationTime() == null) {
            throw new IllegalArgumentException(
                    "First notification time is required");
        }

        switch (settings.getFrequency()) {

            case ONCE -> settings.setSecondNotificationTime(null);

            case TWICE -> {

                if (settings.getSecondNotificationTime() == null) {
                    throw new IllegalArgumentException(
                            "Second notification time is required for TWICE frequency");
                }

                if (settings.getFirstNotificationTime()
                        .equals(settings.getSecondNotificationTime())) {
                    throw new IllegalArgumentException(
                            "Notification times must be different");
                }
            }
        }
    }
}