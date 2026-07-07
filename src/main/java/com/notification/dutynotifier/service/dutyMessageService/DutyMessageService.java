package com.notification.dutynotifier.service.dutyMessageService;

import com.notification.dutynotifier.entity.employee.Employee;
import com.notification.dutynotifier.entity.duty.Duty;
import com.notification.dutynotifier.service.dutyService.DutyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DutyMessageService {

    private final DutyService dutyService;

    public String buildMessage() {

        List<Duty> today = dutyService.findTodayDuties();
        List<Duty> next = dutyService.findNext3DayDuties();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        StringBuilder message = new StringBuilder();
        message.append("🔔 Сьогодні чергують:\n\n");

        for (Duty duty : today) {

            for (Employee employee : duty.getEmployees()) {

                message.append("👤 ")
                        .append(employee.getName())
                        .append("\n");
            }
        }

        message.append("\n📅 Наступні чергування:\n\n");

        for (Duty duty : next) {

            if (duty.getDutyDate()
                    .equals(LocalDate.now())) {
                continue;
            }

            message.append(duty.getDutyDate().format(formatter)).append(" → ");

            for (int i = 0; i < duty.getEmployees().size(); i++) {
                message.append(duty.getEmployees().get(i).getName());

                if (i < duty.getEmployees().size() - 1) {
                    message.append(", ");
                }
            }
            message.append("\n");
        }

        return message.toString();
    }

    public String buildTodayMessage() {

        List<Duty> today = dutyService.findTodayDuties();

        StringBuilder message = new StringBuilder();

        message.append("🔔 Сьогодні чергують:\n\n");

        for (Duty duty : today) {
            for (Employee employee : duty.getEmployees()) {
                message.append("👤 ")
                        .append(employee.getName())
                        .append("\n");
            }
        }

        return message.toString();
    }
}