package com.notification.dutynotifier.service.auditLogService;

import com.notification.dutynotifier.dto.response.AuditLogResponse;
import com.notification.dutynotifier.entity.auditLog.AuditAction;
import com.notification.dutynotifier.entity.auditLog.AuditLog;
import com.notification.dutynotifier.repository.auditLogRepository.AuditLogRepository;
import com.notification.dutynotifier.specification.AuditLogSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository repository;

    public void log(String user, AuditAction action, String details) {

        AuditLog log = AuditLog.builder()
                .createdAt(LocalDateTime.now())
                .userEmail(user)
                .action(action)
                .details(details)
                .build();

        repository.save(log);

    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFailed(String user, AuditAction action, String details) {
        log(user, action, details);
    }

    public Page<AuditLogResponse> getAll(
            LocalDate from,
            LocalDate to,
            AuditAction action,
            Pageable pageable
    ) {

        Specification<AuditLog> spec = Specification.allOf();

        if (from != null) {
            spec = spec.and(AuditLogSpecification.dateFrom(from));
        }

        if (to != null) {
            spec = spec.and(AuditLogSpecification.dateTo(to));
        }

        if (action != null) {
            spec = spec.and(AuditLogSpecification.hasAction(action));
        }

        return repository.findAll(spec, pageable)
                .map(log -> new AuditLogResponse(
                        log.getId(),
                        log.getCreatedAt(),
                        log.getUserEmail(),
                        log.getAction().name(),
                        log.getDetails()
                ));
    }
}
