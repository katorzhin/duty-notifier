package com.notification.dutynotifier.entity.auditLog.messages;

import com.notification.dutynotifier.entity.employee.Employee;

public final class EmployeeAuditMessages {

    private EmployeeAuditMessages() {
    }

    public static String created(Employee employee) {
        return "Created " + format(employee);
    }

    public static String updated(String oldEmployee, Employee newEmployee) {
        return """
                Edited from:
                %s

                To:
                %s
                """.formatted(
                oldEmployee,
                format(newEmployee)
        );
    }

    public static String deleted(Employee employee) {
        return "Deleted " + format(employee);
    }

    public static String format(Employee employee) {

        return employee.getName()
                + " (" + employee.getEmail() + ")";
    }
}