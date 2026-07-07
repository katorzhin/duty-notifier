package com.notification.dutynotifier.service.excelImportService;

import com.notification.dutynotifier.entity.auditLog.AuditAction;
import com.notification.dutynotifier.entity.auditLog.messages.ExcelImportAuditMessages;
import com.notification.dutynotifier.entity.employee.Employee;
import com.notification.dutynotifier.entity.duty.Duty;
import com.notification.dutynotifier.exception.DutyConflictException;
import com.notification.dutynotifier.repository.employeeRepository.EmployeeRepository;
import com.notification.dutynotifier.repository.dutyRepository.DutyRepository;
import com.notification.dutynotifier.service.auditLogService.AuditLogService;
import com.notification.dutynotifier.service.securityService.SecurityService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelImportService {

    private final DutyRepository dutyRepository;
    private final EmployeeRepository employeeRepository;
    private final AuditLogService auditLogService;
    private final SecurityService securityService;

    @Transactional
    public void importExcel(MultipartFile file, boolean replace) {

        try (InputStream inputStream = file.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<LocalDate> conflicts = new ArrayList<>();
            int created = 0;
            int replaced = 0;

            for (Row row : sheet) {

                if (isEmptyRow(row)) {
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

                Duty duty = buildDuty(row);

                dutyRepository.save(duty);
                created++;
            }

            if (!conflicts.isEmpty()) {
                throw new DutyConflictException(conflicts);
            }

            auditLogService.log(
                    securityService.getCurrentUserEmail(),
                    AuditAction.SCHEDULE_UPLOADED,
                    ExcelImportAuditMessages.uploaded(
                            file.getOriginalFilename(), created, replaced));

        } catch (DutyConflictException e) {
            throw e;

        } catch (Exception e) {
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

    private Employee getOrCreateEmployee(String employeeName) {
        return employeeRepository.findByName(employeeName)
                .orElseGet(() -> employeeRepository
                        .save(Employee.builder()
                                .name(employeeName)
                                .build()));
    }

    private Duty buildDuty(Row row) {
        LocalDate dutyDate = extractDate(row);

        Employee firstEmployee = getOrCreateEmployee(row.getCell(1)
                .getStringCellValue()
                .trim());

        Employee secondEmployee = getOrCreateEmployee(row.getCell(2)
                .getStringCellValue()
                .trim());

        return Duty.builder()
                .dutyDate(dutyDate)
                .employees(List.of(firstEmployee, secondEmployee))
                .build();
    }

    private boolean isEmptyRow(Row row) {
        if (row.getCell(0) == null) {
            return true;
        }
        return row.getCell(0).getCellType().name().equals("BLANK");
    }
}