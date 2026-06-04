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

                if (row.getCell(0) == null) {
                    continue;
                }

                if (row.getCell(0).getCellType().name().equals("BLANK")) {
                    continue;
                }

                LocalDate dutyDate =
                        row.getCell(0)
                                .getDateCellValue()
                                .toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();

                String firstUserName = row.getCell(1)
                        .getStringCellValue()
                        .trim();

                String secondUserName = row.getCell(2)
                        .getStringCellValue()
                        .trim();

                User firstUser =
                        userRepository.findByName(firstUserName)
                                .orElseGet(() ->
                                        userRepository.save(
                                                User.builder()
                                                        .name(firstUserName)
                                                        .build()));

                User secondUser =
                        userRepository.findByName(secondUserName)
                                .orElseGet(() ->
                                        userRepository.save(
                                                User.builder()
                                                        .name(secondUserName)
                                                        .build()));

                Duty duty = Duty.builder()
                        .dutyDate(dutyDate)
                        .users(List.of(firstUser, secondUser))
                        .build();
                dutyRepository.save(duty);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}