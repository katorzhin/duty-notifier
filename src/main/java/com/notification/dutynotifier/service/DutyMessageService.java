package com.notification.dutynotifier.service;

import com.notification.dutynotifier.entity.User;
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

        List<Duty> today = dutyService.getTodayDuty();
        List<Duty> next = dutyService.getNext5Days();

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

            if (duty.getDutyDate()
                    .equals(LocalDate.now())) {
                continue;
            }

            message.append(duty.getDutyDate().format(formatter)).append(" → ");

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

    public String buildTodayMessage() {

        List<Duty> today = dutyService.getTodayDuty();

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