package com.notification.dutynotifier.bot;

import com.notification.dutynotifier.service.dutyMessageService.DutyMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotCommandService {

    private final DutyMessageService dutyMessageService;

    public String processCommand(String command) {

        return switch (command) {

            case "/start" -> BotMessages.START_MESSAGE;

            case "/schedule" -> dutyMessageService.buildMessage();

            case "/today" -> dutyMessageService.buildTodayMessage();

            case "/help" -> BotMessages.HELP_MESSAGE;

            default -> BotMessages.UNKNOWN_COMMAND;
        };
    }
}