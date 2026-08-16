package com.notification.dutynotifier.bot;

import com.notification.dutynotifier.service.dutyMessageService.DutyMessageService;
import com.notification.dutynotifier.service.notificationTemplateService.NotificationTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotCommandService {

    private final DutyMessageService dutyMessageService;
    private final NotificationTemplateService notificationTemplateService;

    public String processCommand(String command) {

        return switch (command) {

            case "/start" -> BotMessages.START_MESSAGE;

            case "/schedule" -> dutyMessageService.buildScheduleMessage(
                    notificationTemplateService
                            .getTemplate()
                            .getScheduleTemplate());

            case "/today" -> dutyMessageService.buildTodayMessage(
                    notificationTemplateService
                            .getTemplate()
                            .getTodayTemplate());

            case "/help" -> BotMessages.HELP_MESSAGE;

            default -> BotMessages.UNKNOWN_COMMAND;
        };
    }
}