package ru.hse.cs.java2020.task03.model;

public class BotUser {

    private final Long chatId;
    private Integer orgId;
    private String token;

    public BotUser(Long chatId) {
        this.chatId = chatId;
        this.orgId = 0;
        this.token = "";
    }

    public BotUser(Long chatId, Integer orgId, String token) {
        this.chatId = chatId;
        this.orgId = orgId;
        this.token = token;
    }

    public Long getChatId() {
        return chatId;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public String getToken() {
        return token;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
