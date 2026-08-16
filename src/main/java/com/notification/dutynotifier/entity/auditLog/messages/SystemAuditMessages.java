package com.notification.dutynotifier.entity.auditLog.messages;

public final class SystemAuditMessages {

    private SystemAuditMessages() {
    }

    public static String scheduled(long subscribersCount) {
        return "Scheduled duty notification sent to " + subscribersCount + " subscribers";
    }

    public static String login(String email) {
        return "User logged in: " + email;
    }

    public static String created(String email) {
        return "User created: " + email;
    }

    public static String updated(String email) {
        return "User updated: " + email;
    }

    public static String deleted(String email) {
        return "User deleted: " + email;
    }

    public static String passwordChanged(String email) {
        return "Password changed for user: " + email;
    }

    public static String passwordReset(
            String adminEmail,
            String userEmail
    ) {
        return "Password reset by " + adminEmail + " for " + userEmail;
    }
}