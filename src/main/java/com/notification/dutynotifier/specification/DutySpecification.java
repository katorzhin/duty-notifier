package com.notification.dutynotifier.specification;

import com.notification.dutynotifier.entity.duty.Duty;
import com.notification.dutynotifier.entity.employee.Employee;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

public class DutySpecification {

    public static Specification<Duty> dateFrom(LocalDate from) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("dutyDate"), from);
    }

    public static Specification<Duty> dateTo(LocalDate to) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("dutyDate"), to);
    }

    public static Specification<Duty> hasEmployees(List<Long> employeeIds) {
        return (root, query, cb) -> {

            if (query != null) {
                query.distinct(true);
            }

            Join<Duty, Employee> employees = root.join("employees");

            return employees.get("id").in(employeeIds);
        };
    }

    public static Specification<Duty> dateBetween(LocalDate from, LocalDate to) {
        return Specification.allOf(dateFrom(from), dateTo(to));
    }
}