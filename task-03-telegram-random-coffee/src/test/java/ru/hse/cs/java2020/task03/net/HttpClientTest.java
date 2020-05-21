package ru.hse.cs.java2020.task03.net;

import org.junit.Test;
import ru.hse.cs.java2020.task03.core.BotMessage;

import javax.ws.rs.core.Response;

import static org.junit.Assert.*;



public class HttpClientTest {

    @Test
    public void POST() throws Exception {
        Response res = HttpClient.POST(
                "https://api.telegram.org/bot1136529264:AAHgC7fsp_VB-DAexeYIBgiq-0-QmV95c7g/sendMessage",
                new BotMessage(406192263, "Hi, I am Bot!")
        );
        String responseAsString = res.toString();
        System.out.println(responseAsString);

    }
}