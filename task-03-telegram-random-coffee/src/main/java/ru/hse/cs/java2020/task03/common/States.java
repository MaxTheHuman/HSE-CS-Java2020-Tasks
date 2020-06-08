package ru.hse.cs.java2020.task03.common;

public enum States {
    STOP,
    ORG_ID_NEEDED,
    ORG_ID_UPDATE,
    OAUTH_TOKEN_NEEDED,
    OAUTH_TOKEN_UPDATE,
    MAIN_MENU,
    ILLEGAL_STATE,
    SEARCH_BY_KEY,
    CREATE_TASK;
}
