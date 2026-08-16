package com.notification.dutynotifier.exception;

import java.util.List;

public class EmployeesNotFoundException extends RuntimeException {

    public EmployeesNotFoundException(List<String> employees) {
        super("The following employees were not found: "
                + String.join(", ", employees));
    }
}