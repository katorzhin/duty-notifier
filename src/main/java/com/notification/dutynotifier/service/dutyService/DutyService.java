package com.notification.dutynotifier.service.dutyService;

import com.notification.dutynotifier.dto.dutyRequest.DutyRequest;
import com.notification.dutynotifier.dto.response.DutyResponse;
import com.notification.dutynotifier.entity.auditLog.AuditAction;
import com.notification.dutynotifier.entity.auditLog.messages.DutyAuditMessages;
import com.notification.dutynotifier.entity.duty.Duty;
import com.notification.dutynotifier.entity.employee.Employee;
import com.notification.dutynotifier.exception.DutyNotFoundException;
import com.notification.dutynotifier.exception.EmployeeNotFoundException;
import com.notification.dutynotifier.mapper.DutyMapper;
import com.notification.dutynotifier.repository.dutyRepository.DutyRepository;
import com.notification.dutynotifier.repository.employeeRepository.EmployeeRepository;
import com.notification.dutynotifier.service.auditLogService.AuditLogService;
import com.notification.dutynotifier.service.securityService.SecurityService;
import com.notification.dutynotifier.specification.DutySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DutyService {

    private final DutyRepository dutyRepository;
    private final EmployeeRepository employeeRepository;
    private final DutyMapper dutyMapper;
    private final AuditLogService auditLogService;
    private final SecurityService securityService;

    public DutyResponse create(DutyRequest request) {

        List<Employee> employees = getEmployees(request.getEmployeeIds());

        Duty duty = Duty.builder()
                .employees(employees)
                .dutyDate(request.getDutyDate())
                .build();

        if (request.getEmployeeIds().size() !=
                new HashSet<>(request.getEmployeeIds()).size()) {

            throw new IllegalArgumentException("Duplicate employees selected");
        }

        Duty savedDuty = dutyRepository.save(duty);

        auditLogService.log(
                securityService.getCurrentUserEmail(),
                AuditAction.DUTY_CREATED,
                DutyAuditMessages.created(savedDuty)
        );

        return dutyMapper.toResponse(savedDuty);
    }

    public DutyResponse update(Long id, DutyRequest request) {

        Duty duty = dutyRepository.findById(id)
                .orElseThrow(() -> new DutyNotFoundException("Duty not found"));

        String oldDuty = DutyAuditMessages.format(duty);
        List<Employee> employees = getEmployees(request.getEmployeeIds());

        duty.setDutyDate(request.getDutyDate());
        duty.setEmployees(employees);

        Duty updatedDuty = dutyRepository.save(duty);

        auditLogService.log(
                securityService.getCurrentUserEmail(),
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
                securityService.getCurrentUserEmail(),
                AuditAction.DUTY_DELETED,
                DutyAuditMessages.deleted(duty)
        );
    }

    private List<Employee> getEmployees(List<Long> employeeIds) {
        List<Employee> employees = employeeRepository.findAllById(employeeIds);

        if (employees.size() != employeeIds.size()) {
            throw new EmployeeNotFoundException("Some employees were not found");
        }

        return employees;
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
}