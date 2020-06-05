package ru.hse.cs.java2020.task03;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.hse.cs.java2020.task03.model.BotUser;
import ru.hse.cs.java2020.task03.service.UserService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
 *
 * This is Telegram bot for communicating with Yandex Tracker
 *
 */
@Component
public class TelegramBot extends TelegramLongPollingBot{

    private enum states {
        STOP,
        START,
        ORG_ID_NEEDED,
        OAUTH_TOKEN_NEEDED,
        REGISTERED
    };

    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    private states state = states.STOP;

    @Autowired
    private UserService userService;

    @Value("${bot.token}")
    private String token;

    @Value("${bot.username}")
    private String username;

    @Value("${interface.welcomeMessage}")
    private String welcomeMessage;

    @Value("${interface.messageToStart}")
    private String messageToStart;

    @Value("${interface.stopMessage}")
    private String stopMessage;

    @Value("${interface.infoMessage}")
    private String infoMessage;

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            SendMessage response = new SendMessage();
            Long chatId = message.getChatId();
            response.setChatId(chatId);
            String text = message.getText();

            if (text.contains("Stop")) {
                text = stopMessage;
                state = states.STOP;
            } else if (state.equals(states.STOP) && text.contains("Start")) {
                text = welcomeMessage;
                int addResult = userService.addUser(new BotUser(chatId));
                if (addResult == 1) {
                    text += "I have successfully saved our chat id in my db!\n";
                    state = states.ORG_ID_NEEDED;
                } else {
                    text += "Something went wrong when saving our chat id in db\naddResult = " +
                            Integer.toString(addResult) + "\nPlease try again\n";
                }
            } else if (state.equals(states.ORG_ID_NEEDED)) {
                int orgId;
                try {
                    orgId = Integer.parseInt(text);
                    int updateResult = userService.updateUserOrgId(chatId, orgId);
                    if (updateResult == 1) {
                        text = "I have successfully saved your organisation id in my db!\n";
                        text += "Send me OAuth token for further work\n";
                        state = states.OAUTH_TOKEN_NEEDED;
                    } else {
                        text += "Something went wrong when saving our chat id in db\nupdateResult = " +
                                Integer.toString(updateResult) + "\nPlease try again\n";
                    }
                } catch (NumberFormatException e) {
                    text = "Please enter only digit form of your organisation id";
                }
            } else if (state.equals(states.OAUTH_TOKEN_NEEDED)) {

            } else if (state.equals(states.STOP) && text.contains("Info")) {
                text = infoMessage;
            } else if (text.contains("Forget me")){
                if (userService.forgetUser(chatId) == 1) {
                    text = "I erased all information about you";
                } else {
                    text = "Something went wrong while erasing your information\n" +
                            "Please, try again";
                }

            } else {
                text = "";
                List<BotUser> allUsers = userService.getAllUsers();
                for (BotUser botUser : allUsers) {
                    text += botUser.getChatId().toString() + " " +
                            botUser.getOrgId().toString() + " " +
                            botUser.getToken() + "\n";
                }
            }
            text += "Current state is " + state.toString() + "\n";
            response.setText(text);
            setButtons(response);
            try {
                execute(response);
                logger.info("Sent message \"{}\" to {}", text, chatId);
            } catch (TelegramApiException e) {
                logger.error("Failed to send message \"{}\" to {} due to error: {}", text, chatId, e.getMessage());
            }
        }
    }

    private synchronized void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        if (state.equals(states.STOP)) {
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(true);

            KeyboardRow keyboardFirstRow = new KeyboardRow();
            keyboardFirstRow.add(new KeyboardButton("Start"));

            KeyboardRow keyboardSecondRow = new KeyboardRow();
            keyboardSecondRow.add(new KeyboardButton("Info"));

            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);

            Optional<BotUser> botUser = userService.getUserByChatId(Long.parseLong(sendMessage.getChatId()));
            if (botUser.isPresent()) {
                logger.info("Enter in if");
                KeyboardRow keyboardThirdRow = new KeyboardRow();
                keyboardThirdRow.add(new KeyboardButton("Forget me"));
                keyboard.add(keyboardThirdRow);
            }
        }

        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    @PostConstruct
    public void start() {
        logger.info("username: {}, token: {}", username, token);
    }

}
