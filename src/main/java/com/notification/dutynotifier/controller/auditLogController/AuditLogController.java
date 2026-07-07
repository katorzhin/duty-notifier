package com.notification.dutynotifier.controller.auditLogController;

import com.notification.dutynotifier.dto.response.AuditLogResponse;
import com.notification.dutynotifier.entity.auditLog.AuditAction;
import com.notification.dutynotifier.service.auditLogService.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public Page<AuditLogResponse> getAll(

            @RequestParam(required = false)
            LocalDate from,
            @RequestParam(required = false)
            LocalDate to,
            @RequestParam(required = false)
            AuditAction action,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return auditLogService.getAll(from, to, action, pageable);
    }

    @GetMapping("/actions")
    public List<String> getActions() {
        return Arrays.stream(AuditAction.values())
                .map(Enum::name)
                .toList();
    }
}