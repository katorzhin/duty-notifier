package com.notification.dutynotifier.service.excelImportService;

import com.notification.dutynotifier.entity.auditLog.AuditAction;
import com.notification.dutynotifier.entity.auditLog.messages.ExcelImportAuditMessages;
import com.notification.dutynotifier.entity.employee.Employee;
import com.notification.dutynotifier.entity.duty.Duty;
import com.notification.dutynotifier.exception.DutyConflictException;
import com.notification.dutynotifier.exception.EmployeesNotFoundException;
import com.notification.dutynotifier.repository.employeeRepository.EmployeeRepository;
import com.notification.dutynotifier.repository.dutyRepository.DutyRepository;
import com.notification.dutynotifier.service.auditLogService.AuditLogService;
import com.notification.dutynotifier.service.securityService.AuthenticatedUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelImportService {

    private final DutyRepository dutyRepository;
    private final EmployeeRepository employeeRepository;
    private final AuditLogService auditLogService;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional
    public void importExcel(MultipartFile file, boolean replace) {

        try (InputStream inputStream = file.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            Map<String, Employee> employeeMap =
                    employeeRepository.findAll()
                            .stream()
                            .collect(Collectors.toMap(
                                    Employee::getName,
                                    employee -> employee
                            ));

            List<String> missingEmployees = validateEmployees(sheet, employeeMap);

            if (!missingEmployees.isEmpty()) {
                throw new EmployeesNotFoundException(missingEmployees);
            }
            List<LocalDate> conflicts = new ArrayList<>();
            int created = 0;
            int replaced = 0;

            for (Row row : sheet) {

                if (row.getRowNum() == 0 || isEmptyRow(row)) {
                    continue;
                }

                LocalDate dutyDate = extractDate(row);

                List<Duty> existingDuties = dutyRepository.findByDutyDate(dutyDate);

                if (!existingDuties.isEmpty()) {

                    if (!replace) {
                        conflicts.add(dutyDate);
                        continue;
                    }

                    dutyRepository.deleteAll(existingDuties);
                    replaced += existingDuties.size();
                }

                Duty duty = buildDuty(row, employeeMap);

                dutyRepository.save(duty);
                created++;
            }

            if (!conflicts.isEmpty()) {
                throw new DutyConflictException(conflicts);
            }

            auditLogService.log(
                    authenticatedUserService.getCurrentUserEmail(),
                    AuditAction.SCHEDULE_UPLOADED,
                    ExcelImportAuditMessages.uploaded(
                            file.getOriginalFilename(), created, replaced));

        } catch (DutyConflictException | EmployeesNotFoundException e) {
            log.warn("Schedule upload rejected for file: {}. {}",
                    file.getOriginalFilename(), e.getMessage());
            auditLogService.logFailed(
                    authenticatedUserService.getCurrentUserEmail(),
                    AuditAction.SCHEDULE_UPLOAD_FAILED,
                    ExcelImportAuditMessages.failed(file.getOriginalFilename(), e.getMessage()));
            throw e;

        } catch (Exception e) {
            log.error("Failed to import schedule from file {}", file.getOriginalFilename(), e);

            auditLogService.logFailed(
                    authenticatedUserService.getCurrentUserEmail(),
                    AuditAction.SCHEDULE_UPLOAD_FAILED,
                    ExcelImportAuditMessages.failed(file.getOriginalFilename(), e.getMessage()));

            throw new RuntimeException("Failed to import excel", e);
        }
    }

    private LocalDate extractDate(Row row) {
        return row.getCell(0)
                .getDateCellValue()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private Employee getEmployee(String employeeName, Map<String, Employee> employees) {
        return employees.get(employeeName);
    }

    private Duty buildDuty(Row row,
                           Map<String, Employee> employeeMap) {

        LocalDate dutyDate = extractDate(row);

        List<Employee> employees = new ArrayList<>();

        for (int cellIndex = 1;
             cellIndex < row.getLastCellNum();
             cellIndex++) {

            if (row.getCell(cellIndex) == null) {
                continue;
            }

            String employeeName = row.getCell(cellIndex)
                    .getStringCellValue()
                    .trim();

            if (employeeName.isEmpty()) {
                continue;
            }

            employees.add(getEmployee(employeeName, employeeMap));
        }

        if (employees.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one employee is required"
            );
        }

        return Duty.builder()
                .dutyDate(dutyDate)
                .employees(employees)
                .build();
    }

    private List<String> validateEmployees(
            Sheet sheet,
            Map<String, Employee> employeeMap
    ) {


        Set<String> missingEmployees = new LinkedHashSet<>();

        for (Row row : sheet) {
            if (row.getRowNum() == 0 || isEmptyRow(row)) {
                continue;
            }

            for (int cellIndex = 1;
                 cellIndex < row.getLastCellNum();
                 cellIndex++) {

                if (row.getCell(cellIndex) == null) {
                    continue;
                }

                String employeeName = row.getCell(cellIndex)
                        .getStringCellValue()
                        .trim();

                if (employeeName.isEmpty()) {
                    continue;
                }

                if (!employeeMap.containsKey(employeeName)) {
                    missingEmployees.add(employeeName);
                }
            }
        }

        return new ArrayList<>(missingEmployees);
    }

    private boolean isEmptyRow(Row row) {
        if (row.getCell(0) == null) {
            return true;
        }
        return row.getCell(0).getCellType() == CellType.BLANK;
    }
}