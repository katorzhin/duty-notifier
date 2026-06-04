package com.notification.dutynotifier.bot;

public final class BotMessages {

    private BotMessages() {}

    public static final String START_MESSAGE =
            """
                    👋 Вітаю!
                    
                    Доступні команди:
                    
                    /today - хто чергує сьогодні
                    
                    /schedule - графік на найближчі дні
                    
                    /help - допомога
                    """;

    public static final String HELP_MESSAGE =
            """
                    Доступні команди:
                    
                    /today
                    /schedule
                    /help
                    """;

    public static final String UNKNOWN_COMMAND = "Невідома команда. Використайте /help";
}