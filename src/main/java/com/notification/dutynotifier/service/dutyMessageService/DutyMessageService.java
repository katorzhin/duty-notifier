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

    public String buildTodayMessage(String template) {

        return template.replace(
                "{{TODAY}}",
                buildTodayDuties()
        );
    }

    public String buildScheduleMessage(String template) {
        return template.replace(
                "{{NEXT_DUTIES}}",
                buildUpcomingDuties()
        );
    }

    private String buildUpcomingDuties() {

        List<Duty> next = dutyService.findNext3DayDuties();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        StringBuilder result = new StringBuilder();

        for (Duty duty : next) {

            if (duty.getDutyDate().equals(LocalDate.now())) {
                continue;
            }

            result.append(duty.getDutyDate().format(formatter)).append(" → ");

            for (int i = 0; i < duty.getEmployees().size(); i++) {

                result.append(duty.getEmployees().get(i).getName());

                if (i < duty.getEmployees().size() - 1) {
                    result.append(", ");
                }
            }

            result.append("\n");
        }

        return result.toString();
    }

    private String buildTodayDuties() {

        List<Duty> today = dutyService.findTodayDuties();

        StringBuilder result = new StringBuilder();

        for (Duty duty : today) {
            for (Employee employee : duty.getEmployees()) {
                result.append("👤 ")
                        .append(employee.getName())
                        .append("\n");
            }
        }

        return result.toString();
    }
}