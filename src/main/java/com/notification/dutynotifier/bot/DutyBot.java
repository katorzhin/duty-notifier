package com.notification.dutynotifier.bot;

import com.notification.dutynotifier.config.TelegramConfig;
import com.notification.dutynotifier.entity.subscriber.Subscriber;
import com.notification.dutynotifier.repository.subscriberRepository.SubscriberRepository;
import com.notification.dutynotifier.service.dutyMessageService.DutyMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class DutyBot implements SpringLongPollingBot {

    private final TelegramConfig config;
    private final TelegramClient telegramClient;
    private final SubscriberRepository subscriberRepository;
    private final DutyMessageService dutyMessageService;

    public DutyBot(TelegramConfig config,
                   SubscriberRepository subscriberRepository,
                   DutyMessageService dutyMessageService) {
        this.config = config;
        this.subscriberRepository = subscriberRepository;
        this.dutyMessageService = dutyMessageService;
        this.telegramClient = new OkHttpTelegramClient(config.getToken());
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {

        return updates -> {

            for (Update update : updates) {
                if (update.hasMessage()) {

                    Long chatId = update.getMessage().getChatId();
                    String text = update.getMessage().getText();

                    if (subscriberRepository.findByChatId(chatId).isEmpty()) {

                        subscriberRepository.save(
                                Subscriber.builder()
                                        .chatId(chatId)
                                        .username(update.getMessage()
                                                .getFrom()
                                                .getUserName())
                                        .firstName(update.getMessage().getFrom().getFirstName())
                                        .lastName(update.getMessage()
                                                .getFrom()
                                                .getLastName())
                                        .build());
                    }

                    switch (text) {

                        case "/start" -> sendStartMessage(chatId);

                        case "/schedule" -> sendMessage(chatId, dutyMessageService.buildMessage());

                        case "/today" -> sendMessage(chatId, dutyMessageService.buildTodayMessage());

                        case "/help" -> sendHelpMessage(chatId);

                        default -> sendMessage(chatId, "Невідома команда. Використайте /help");
                    }

                    System.out.println(
                            "Message: " + update.getMessage().getText());

                    System.out.println(
                            "ChatId: " + chatId);
                }
            }
        };
    }

    public void sendMessage(Long chatId, String text) {

        SendMessage message =
                SendMessage.builder()
                        .chatId(chatId)
                        .text(text)
                        .build();

        try {
            telegramClient.execute(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendStartMessage(Long chatId) {

        sendMessage(
                chatId,
                """
                        👋 Вітаю!
                        
                        Доступні команди:
                        
                        /today - хто чергує сьогодні
                        
                        /schedule - графік на найближчі дні
                        
                        /help - допомога
                        """
        );
    }

    private void sendHelpMessage(Long chatId) {

        sendMessage(
                chatId,
                """
                        Доступні команди:
                        
                        /today
                        /schedule
                        /help
                        """
        );
    }
}