package com.notification.dutynotifier.exception;

public class DutyNotFoundException extends RuntimeException {
    public DutyNotFoundException(String message) {
        super(message);
    }
}