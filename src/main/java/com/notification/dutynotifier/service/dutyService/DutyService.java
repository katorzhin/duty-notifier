package com.notification.dutynotifier.service.dutyService;

import com.notification.dutynotifier.dto.dutyRequest.DutyRequest;
import com.notification.dutynotifier.dto.response.DutyResponse;
import com.notification.dutynotifier.entity.auditLog.AuditAction;
import com.notification.dutynotifier.entity.auditLog.messages.DutyAuditMessages;
import com.notification.dutynotifier.entity.duty.Duty;
import com.notification.dutynotifier.entity.employee.Employee;
import com.notification.dutynotifier.entity.employee.EmployeeStatus;
import com.notification.dutynotifier.exception.DutyNotFoundException;
import com.notification.dutynotifier.exception.EmployeeNotFoundException;
import com.notification.dutynotifier.mapper.DutyMapper;
import com.notification.dutynotifier.repository.dutyRepository.DutyRepository;
import com.notification.dutynotifier.repository.employeeRepository.EmployeeRepository;
import com.notification.dutynotifier.service.auditLogService.AuditLogService;
import com.notification.dutynotifier.service.securityService.AuthenticatedUserService;
import com.notification.dutynotifier.specification.DutySpecification;
import com.notification.dutynotifier.dto.dutyRequest.GenerateDutyRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DutyService {

    private static final int MIN_EMPLOYEES_PER_DUTY = 1;
    private static final int MAX_EMPLOYEES_PER_DUTY = 3;

    private final DutyRepository dutyRepository;
    private final EmployeeRepository employeeRepository;
    private final DutyMapper dutyMapper;
    private final AuditLogService auditLogService;
    private final AuthenticatedUserService authenticatedUserService;

    public DutyResponse create(DutyRequest request) {

        validateDuplicateEmployees(request.getEmployeeIds());
        List<Employee> employees = getEmployeeList(request.getEmployeeIds());

        Duty duty = Duty.builder()
                .employees(employees)
                .dutyDate(request.getDutyDate())
                .build();

        Duty savedDuty = dutyRepository.save(duty);

        auditLogService.log(
                authenticatedUserService.getCurrentUserEmail(),
                AuditAction.DUTY_CREATED,
                DutyAuditMessages.created(savedDuty)
        );

        return dutyMapper.toResponse(savedDuty);
    }

    public DutyResponse update(Long id, DutyRequest request) {

        Duty duty = dutyRepository.findById(id)
                .orElseThrow(() -> new DutyNotFoundException("Duty not found"));

        String oldDuty = DutyAuditMessages.format(duty);
        validateDuplicateEmployees(request.getEmployeeIds());
        List<Employee> employees = getEmployeeList(request.getEmployeeIds());

        duty.setDutyDate(request.getDutyDate());
        duty.setEmployees(employees);

        Duty updatedDuty = dutyRepository.save(duty);

        auditLogService.log(
                authenticatedUserService.getCurrentUserEmail(),
                AuditAction.DUTY_UPDATED,
                DutyAuditMessages.updated(oldDuty, updatedDuty)
        );

        return dutyMapper.toResponse(updatedDuty);
    }

    public void delete(Long id) {

        Duty duty = dutyRepository.findById(id)
                .orElseThrow(() ->
                        new DutyNotFoundException("Duty not found"));

        dutyRepository.delete(duty);

        auditLogService.log(
                authenticatedUserService.getCurrentUserEmail(),
                AuditAction.DUTY_DELETED,
                DutyAuditMessages.deleted(duty)
        );
    }

    private List<Employee> getEmployeeList(List<Long> employeeIds) {
        List<Employee> employees = employeeRepository.findAllById(employeeIds);

        if (employees.size() != employeeIds.size()) {
            throw new EmployeeNotFoundException("Some employees were not found");
        }
        return employees;
    }

    private List<Employee> getEmployeesInOrder(List<Long> employeeIds) {

        List<Employee> employees = getEmployeeList(employeeIds);

        return employeeIds.stream()
                .map(id -> employees.stream()
                        .filter(employee -> employee.getId().equals(id))
                        .findFirst()
                        .orElseThrow(() -> new EmployeeNotFoundException("Employee not found")))
                .toList();
    }

    public Page<DutyResponse> getAll(LocalDate from, LocalDate to,
                                     List<Long> employeeIds, Pageable pageable) {

        Specification<Duty> spec = Specification.allOf();


        if (from != null) {
            spec = spec.and(DutySpecification.dateFrom(from));
        }

        if (to != null) {
            spec = spec.and(DutySpecification.dateTo(to));
        }

        if (employeeIds != null && !employeeIds.isEmpty()) {

            spec = spec.and(DutySpecification.hasEmployees(employeeIds));
        }

        return dutyRepository.findAll(spec, pageable).map(dutyMapper::toResponse);
    }

    public List<DutyResponse> getTodayDuty() {
        return dutyRepository.findByDutyDate(LocalDate.now())
                .stream()
                .map(dutyMapper::toResponse)
                .toList();
    }

    public List<DutyResponse> getNext3Days() {
        return dutyRepository.findByDutyDateBetweenOrderByDutyDateAsc(
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(3))
                .stream()
                .map(dutyMapper::toResponse)
                .toList();
    }

    public List<Duty> findTodayDuties() {
        return dutyRepository.findByDutyDate(
                LocalDate.now()
        );
    }

    public List<Duty> findNext3DayDuties() {
        return dutyRepository.findByDutyDateBetweenOrderByDutyDateAsc(
                LocalDate.now(),
                LocalDate.now().plusDays(3)
        );
    }

    @Transactional
    public void generate(GenerateDutyRequest request) {

        LocalDate endDate = request.getStartDate()
                .plusDays(request.getDays() - 1);

        validateGenerateRequest(request, endDate);

        List<Employee> employees = getEmployeesInOrder(request.getEmployeeIds());

        validateActiveEmployees(employees);

        List<Duty> duties = new ArrayList<>();
        int employeeIndex = 0;

        for (int day = 0; day < request.getDays(); day++) {
            List<Employee> dutyEmployees = new ArrayList<>();

            for (int i = 0; i < request.getEmployeesPerDuty(); i++) {
                dutyEmployees.add(employees.get(employeeIndex));
                employeeIndex = (employeeIndex + 1) % employees.size();
            }

            duties.add(Duty.builder()
                    .dutyDate(request.getStartDate().plusDays(day))
                    .employees(dutyEmployees)
                    .build());
        }

        dutyRepository.saveAll(duties);
    }

    private void validateGenerateRequest(GenerateDutyRequest request, LocalDate endDate) {

        if (request.getEmployeesPerDuty() < MIN_EMPLOYEES_PER_DUTY
                || request.getEmployeesPerDuty() > MAX_EMPLOYEES_PER_DUTY) {
            throw new IllegalArgumentException(
                    "Employees per duty must be between 1 and 3");
        }

        validateDuplicateEmployees(request.getEmployeeIds());

        if (request.getEmployeesPerDuty() > request.getEmployeeIds().size()) {
            throw new IllegalArgumentException(
                    "Employees per duty cannot exceed the number of selected employees");
        }

        boolean exists = dutyRepository.count(
                DutySpecification.dateBetween(request.getStartDate(), endDate)
        ) > 0;

        if (exists) {
            throw new IllegalArgumentException(
                    "Duties already exist in the selected date range");
        }
    }

    private void validateDuplicateEmployees(List<Long> employeeIds) {
        if (employeeIds.size() != new HashSet<>(employeeIds).size()) {
            throw new IllegalArgumentException("Duplicate employees selected");
        }
    }

    private void validateActiveEmployees(List<Employee> employees) {

        boolean hasInactiveEmployees = employees.stream()
                .anyMatch(employee -> employee.getStatus() == EmployeeStatus.INACTIVE);

        if (hasInactiveEmployees) {
            throw new IllegalArgumentException(
                    "Inactive employees cannot be selected for duty generation");
        }
    }
}