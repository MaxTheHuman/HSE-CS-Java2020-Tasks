package ru.hse.cs.java2020.task03.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnClass(TelegramBotsApi.class)
public class TelegramBotAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    @Autowired
    private TelegramBot bot;

    static {
        ApiContextInitializer.init();
    }

    @PostConstruct
    public void start() throws TelegramApiRequestException {
        TelegramBotsApi api = new TelegramBotsApi();
        try {
            logger.info("Registering polling bot: {}", bot.getBotUsername());
            api.registerBot(bot);
        } catch (TelegramApiException e) {
            logger.error("Failed to register bot {} due to error", bot.getBotUsername(), e);
        }
    }

}
