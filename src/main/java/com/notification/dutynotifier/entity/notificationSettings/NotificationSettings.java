package com.notification.dutynotifier.entity.notificationSettings;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "notification_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSettings {

    @Id
    private Long id;

    @Column(nullable = false)
    private boolean enabled;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationFrequency frequency;

    @Column(nullable = false)
    private LocalTime firstNotificationTime;

    private LocalTime secondNotificationTime;
}