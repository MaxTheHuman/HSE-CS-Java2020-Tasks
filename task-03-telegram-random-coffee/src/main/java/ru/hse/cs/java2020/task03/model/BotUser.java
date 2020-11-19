package ru.hse.cs.java2020.task03.model;

import ru.hse.cs.java2020.task03.common.States;

public class BotUser {

    private final Long chatId;
    private Integer orgId;
    private String token;
    private States state;
    private int page;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public BotUser(Long chatId) {
        this.chatId = chatId;
        this.orgId = 0;
        this.token = "";
        this.state = States.STOP;
        this.page = 1;
    }

    public BotUser(Long chatId, Integer orgId, String token, States state, int page) {
        this.chatId = chatId;
        this.orgId = orgId;
        this.token = token;
        this.state = state;
        this.page = page;
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

    public States getState() {
        return state;
    }

    public String getStateAsString() {
        return state.toString();
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setState(States state) {
        this.state = state;
    }
}
