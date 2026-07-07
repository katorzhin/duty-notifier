package com.notification.dutynotifier.repository.employeeRepository;

import com.notification.dutynotifier.entity.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByName(String name);
}