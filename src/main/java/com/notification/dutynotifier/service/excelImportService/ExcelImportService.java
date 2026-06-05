package com.notification.dutynotifier.service.excelImportService;

import com.notification.dutynotifier.entity.user.User;
import com.notification.dutynotifier.entity.duty.Duty;
import com.notification.dutynotifier.repository.userRepository.UserRepository;
import com.notification.dutynotifier.repository.dutyRepository.DutyRepository;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelImportService {

    private final DutyRepository dutyRepository;
    private final UserRepository userRepository;

    @Transactional
    public void importExcel(MultipartFile file) {

        try (InputStream inputStream = file.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            dutyRepository.deleteAll();

            for (Row row : sheet) {
                if (isEmptyRow(row)) {
                    continue;
                }

                Duty duty = buildDuty(row);

                dutyRepository.save(duty);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private LocalDate extractDate(Row row) {
        return row.getCell(0)
                .getDateCellValue()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private User getOrCreateUser(String userName) {
        return userRepository.findByName(userName)
                .orElseGet(() -> userRepository
                        .save(User.builder()
                                .name(userName)
                                .build()));
    }

    private Duty buildDuty(Row row) {
        LocalDate dutyDate = extractDate(row);

        User firstUser = getOrCreateUser(row.getCell(1)
                .getStringCellValue()
                .trim());

        User secondUser = getOrCreateUser(row.getCell(2)
                .getStringCellValue()
                .trim());

        return Duty.builder()
                .dutyDate(dutyDate)
                .users(List.of(firstUser, secondUser))
                .build();
    }

    private boolean isEmptyRow(Row row) {
        if (row.getCell(0) == null) {
            return true;
        }
        return row.getCell(0).getCellType().name().equals("BLANK");
    }
}