package com.notification.dutynotifier.bot;

import com.notification.dutynotifier.config.TelegramConfig;
import com.notification.dutynotifier.entity.Subscriber;
import com.notification.dutynotifier.repository.SubscriberRepository;
import com.notification.dutynotifier.service.dutyService.DutyService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
//@RequiredArgsConstructor
public class DutyBot implements SpringLongPollingBot {

    private final TelegramConfig config;
    private final TelegramClient telegramClient;
    private final SubscriberRepository subscriberRepository;
    private final DutyService dutyService;

    public DutyBot(TelegramConfig config,
                   SubscriberRepository subscriberRepository,
                   DutyService dutyService) {
        this.config = config;
        this.subscriberRepository = subscriberRepository;
        this.dutyService = dutyService;
        System.out.println("TOKEN = " + config.getToken());
        System.out.println("USERNAME = " + config.getUsername());
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

                    if ("/start".equals(text)) {

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

                    if ("/schedule".equals(text)) {
                        sendMessage(chatId, dutyService.buildMessage());
                    }

                    if ("/help".equals(text)) {

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

                    if ("/today".equals(text)) {
                        sendMessage(chatId, dutyService.buildTodayMessage());
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
}