package ru.hse.cs.java2020.task03.core;

public class YaTrackerBotApi implements UpdateHandler {

    @Override
    public void onUpdate(Update update) {
        int chatId = update.getMessage().getChat().getId();
        String text = update.getMessage().getText();
        String user = update.getMessage().getChat().getFirst_name();

        new BotMessage(chatId, text + " - " + user).send();
    }
}
