package com.notification.dutynotifier.exception;

public class SystemAdminModificationException extends RuntimeException {

    public SystemAdminModificationException() {
        super("System administrator cannot be modified");
    }
}