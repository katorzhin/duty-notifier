package com.notification.dutynotifier.entity.auditLog.messages;

public final class ExcelImportAuditMessages {

    private ExcelImportAuditMessages() {
    }

    public static String uploaded(String fileName, int created, int replaced) {
        return """
                File: %s
                Created: %d
                Replaced: %d
                """.formatted(fileName, created, replaced);
    }
}