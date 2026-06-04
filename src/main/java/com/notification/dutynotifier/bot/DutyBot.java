package com.notification.dutynotifier.bot;

import com.notification.dutynotifier.config.TelegramConfig;
import com.notification.dutynotifier.entity.subscriber.Subscriber;
import com.notification.dutynotifier.repository.subscriberRepository.SubscriberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
public class DutyBot implements SpringLongPollingBot {

    private final TelegramConfig config;
    private final TelegramClient telegramClient;
    private final SubscriberRepository subscriberRepository;
    private final BotCommandService botCommandService;

    public DutyBot(TelegramConfig config,
                   SubscriberRepository subscriberRepository,
                   BotCommandService botCommandService) {
        this.config = config;
        this.subscriberRepository = subscriberRepository;
        this.botCommandService = botCommandService;
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
                if (update.hasMessage() && update.getMessage().hasText()) {

                    Long chatId = update.getMessage().getChatId();
                    String text = update.getMessage().getText();

                    if (subscriberRepository.findByChatId(chatId).isEmpty()) {

                        subscriberRepository.save(buildSubscriber(update, chatId));

                        log.info("New subscriber registered. chatId={}", chatId);
                    }
                    String response = botCommandService.processCommand(text);
                    sendMessage(chatId, response);

                    log.info("Received message '{}' from chatId={}", text, chatId);
                }
            }
        };
    }

    public void sendMessage(Long chatId, String text) {

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();

        try {
            telegramClient.execute(message);
        } catch (Exception e) {
            log.error("Failed to send telegram message to chatId={}", chatId, e);
            throw new RuntimeException(e);
        }
    }

    private Subscriber buildSubscriber(Update update, Long chatId) {
        return Subscriber.builder()
                .chatId(chatId)
                .username(update.getMessage()
                        .getFrom()
                        .getUserName())
                .firstName(update.getMessage().getFrom().getFirstName())
                .lastName(update.getMessage()
                        .getFrom()
                        .getLastName())
                .build();
    }
}