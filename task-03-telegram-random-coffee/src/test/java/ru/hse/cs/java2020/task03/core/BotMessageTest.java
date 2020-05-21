package ru.hse.cs.java2020.task03.core;

import org.junit.Test;

import static org.junit.Assert.*;

public class BotMessageTest {

    @Test
    public void send() throws Exception {
        new BotMessage(406192263, "Hi there again, Bot!").send();
    }
}