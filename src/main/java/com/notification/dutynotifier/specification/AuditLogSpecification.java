package com.notification.dutynotifier.specification;

import com.notification.dutynotifier.entity.auditLog.AuditAction;
import com.notification.dutynotifier.entity.auditLog.AuditLog;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class AuditLogSpecification {

    public static Specification<AuditLog> dateFrom(LocalDate from) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("createdAt"), from.atStartOfDay());
    }

    public static Specification<AuditLog> dateTo(LocalDate to) {
        return (root, query, cb) ->
                cb.lessThan(root.get("createdAt"), to.plusDays(1).atStartOfDay());
    }

    public static Specification<AuditLog> hasAction(AuditAction action) {
        return (root, query, cb) ->
                cb.equal(root.get("action"), action);
    }
}