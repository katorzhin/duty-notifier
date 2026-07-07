package com.notification.dutynotifier.service.employeeService;

import com.notification.dutynotifier.dto.response.EmployeeResponse;
import com.notification.dutynotifier.dto.employeeRequest.EmployeeRequest;
import com.notification.dutynotifier.entity.auditLog.AuditAction;
import com.notification.dutynotifier.entity.auditLog.messages.EmployeeAuditMessages;
import com.notification.dutynotifier.entity.employee.Employee;
import com.notification.dutynotifier.exception.EmployeeNotFoundException;
import com.notification.dutynotifier.mapper.EmployeeMapper;
import com.notification.dutynotifier.repository.dutyRepository.DutyRepository;
import com.notification.dutynotifier.repository.employeeRepository.EmployeeRepository;
import com.notification.dutynotifier.service.auditLogService.AuditLogService;
import com.notification.dutynotifier.service.securityService.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final DutyRepository dutyRepository;
    private final AuditLogService auditLogService;
    private final SecurityService securityService;

    public EmployeeResponse create(EmployeeRequest request) {

        Employee employee = employeeMapper.toEntity(request);

        Employee savedEmployee = employeeRepository.save(employee);

        auditLogService.log(
                securityService.getCurrentUserEmail(),
                AuditAction.EMPLOYEE_CREATED,
                EmployeeAuditMessages.created(savedEmployee)
        );

        return employeeMapper.toResponse(savedEmployee);
    }

    public List<EmployeeResponse> getAll() {
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

    public EmployeeResponse update(Long id, EmployeeRequest request) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found"));

        String oldEmployee = EmployeeAuditMessages.format(employee);

        employee.setName(request.getName());
        employee.setEmail(request.getEmail());

        Employee updatedEmployee = employeeRepository.save(employee);

        auditLogService.log(
                securityService.getCurrentUserEmail(),
                AuditAction.EMPLOYEE_UPDATED,
                EmployeeAuditMessages.updated(oldEmployee, updatedEmployee)
        );

        return employeeMapper.toResponse(updatedEmployee);
    }

    public void delete(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found"));

        if (dutyRepository.existsByEmployees_Id(id)) {
            throw new IllegalStateException("Cannot delete employee because they are assigned to duties.");
        }

        employeeRepository.delete(employee);

        auditLogService.log(
                securityService.getCurrentUserEmail(),
                AuditAction.EMPLOYEE_DELETED,
                EmployeeAuditMessages.deleted(employee)
        );
    }
}