package com.notification.dutynotifier.entity.notificationTemplate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplate {

    @Id
    private Long id;

    private String todayTemplate;

    private String scheduleTemplate;
}