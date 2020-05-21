package ru.hse.cs.java2020.task03.core;

import java.util.Date;

public class Message {

    private int message_id;
    private From from;
    private Chat chat;
    private Date date;
    private String text;

    public int getMessage_id() {
        return message_id;
    }

    public From getFrom() {
        return from;
    }

    public Chat getChat() {
        return chat;
    }

    public Date getDate() {
        return date;
    }

    public String getText() {
        return text;
    }
}
