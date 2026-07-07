package com.notification.dutynotifier.exception;

import java.time.LocalDate;
import java.util.List;

public class DutyConflictException extends RuntimeException {

    private final List<LocalDate> dates;

    public DutyConflictException(
            List<LocalDate> dates
    ) {
        super("Duty dates already exist");
        this.dates = dates;
    }

    public List<LocalDate> getDates() {
        return dates;
    }
}