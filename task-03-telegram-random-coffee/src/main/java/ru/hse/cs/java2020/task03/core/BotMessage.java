package ru.hse.cs.java2020.task03.core;


import ru.hse.cs.java2020.task03.net.HttpClient;

public class BotMessage {

    private static final String TELEGRAM_RESOURCE="https://api.telegram.org/bot1136529264:AAHgC7fsp_VB-DAexeYIBgiq-0-QmV95c7g";

    private final int chat_id;
    private final String text;

    public BotMessage(int chat_id, String text) {
        this.chat_id = chat_id;
        this.text = text;
    }

    public int getChat_id() {
        return chat_id;
    }

    public String getText() {
        return text;
    }

    public void send() {
        HttpClient.POST(this.TELEGRAM_RESOURCE + "/sendMessage", this);
    }
}
