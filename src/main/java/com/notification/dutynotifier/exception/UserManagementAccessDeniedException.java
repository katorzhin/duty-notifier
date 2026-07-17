package com.notification.dutynotifier.exception;

public class UserManagementAccessDeniedException extends RuntimeException {

    public UserManagementAccessDeniedException() {
        super("You do not have permission to perform this action.");
    }
}