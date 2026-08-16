package com.notification.dutynotifier.exception;

public class SelfUserDeletionException extends RuntimeException {

    public SelfUserDeletionException() {
        super("You cannot delete your own account");
    }
}