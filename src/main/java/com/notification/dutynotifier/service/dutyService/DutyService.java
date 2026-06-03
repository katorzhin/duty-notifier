package com.notification.dutynotifier.service.dutyService;

import com.notification.dutynotifier.dto.dutyRequest.DutyRequest;
import com.notification.dutynotifier.entity.duty.Duty;
import com.notification.dutynotifier.entity.User;
import com.notification.dutynotifier.repository.dutyRepository.DutyRepository;
import com.notification.dutynotifier.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DutyService {

    private final DutyRepository dutyRepository;
    private final UserRepository userRepository;

    public Duty create(DutyRequest request) {

        List<User> users =
                userRepository.findAllById(request
                        .getUserIds());

        Duty duty =
                Duty.builder()
                        .users(users)
                        .dutyDate(request.getDutyDate())
                        .build();

        return dutyRepository.save(duty);
    }

    public List<Duty> getAll() {
        return dutyRepository.findAll();
    }

    public List<Duty> getTodayDuty() {
        return dutyRepository.findByDutyDate(
                LocalDate.now()
        );
    }

    public List<Duty> getNext5Days() {
        return dutyRepository.findByDutyDateBetween(
                LocalDate.now(),
                LocalDate.now().plusDays(5)
        );
    }


    public String buildMessage() {

        List<Duty> today = getTodayDuty();
        List<Duty> next = getNext5Days();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        StringBuilder message = new StringBuilder();

        message.append("🔔 Сьогодні чергують:\n\n");

        for (Duty duty : today) {
            for (User user : duty.getUsers()) {
                message.append("👤 ")
                        .append(user.getName())
                        .append("\n");
            }
        }

        message.append("\n📅 Наступні чергування:\n\n");

        for (Duty duty : next) {

            if (duty.getDutyDate().equals(LocalDate.now())) {
                continue;
            }

            message.append(duty.getDutyDate().format(formatter))
                    .append(" → ");

            for (int i = 0; i < duty.getUsers().size(); i++) {

                message.append(duty.getUsers().get(i).getName());

                if (i < duty.getUsers().size() - 1) {
                    message.append(", ");
                }
            }

            message.append("\n");
        }

        return message.toString();
    }

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

    public String buildTodayMessage() {

        List<Duty> today = getTodayDuty();

        StringBuilder message = new StringBuilder();

        message.append("🔔 Сьогодні чергують:\n\n");

        for (Duty duty : today) {

            for (User user : duty.getUsers()) {

                message.append("👤 ")
                        .append(user.getName())
                        .append("\n");
            }
        }

        return message.toString();
    }
}