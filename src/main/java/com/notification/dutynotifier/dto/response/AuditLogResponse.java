package com.notification.dutynotifier.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {

    private Long id;

    private LocalDateTime createdAt;

    private String userEmail;

    private String action;

    private String details;
}