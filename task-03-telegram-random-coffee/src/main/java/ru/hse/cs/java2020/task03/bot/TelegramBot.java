package ru.hse.cs.java2020.task03.bot;

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
import ru.hse.cs.java2020.task03.common.States;
import ru.hse.cs.java2020.task03.model.BotUser;
import ru.hse.cs.java2020.task03.service.UserService;
import ru.hse.cs.java2020.task03.trackerApiHandler.TrackerApiHandler;

import javax.annotation.PostConstruct;
import java.io.IOException;
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

    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    private States state = States.STOP;

    @Autowired
    private UserService userService;

    @Value("${bot.token}")
    private String token;

    @Value("${bot.username}")
    private String username;

    private final String welcomeMessage = "Hi! I'm here to help you contact with Yandex Tracker\n";

    private final String messageToStart = "Send 'Start' to start using this bot\n";

    private String stopMessage = "Come back again later!\n";

    private final String infoMessage = "I can do following things:\n" +
            "list all tasks\n" +
            "search for task by key\n" +
            "etc...\n" +
            "You can deactivate me any time by sending 'Stop'\n";

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

            Optional<BotUser> botUser = userService.getUserByChatId(chatId);
            if (botUser.isPresent()) {
                state = botUser.get().getState();
            } else {
                state = States.STOP;
            }

            if (text.toLowerCase().contains("stop")) {
                if (userService.updateUserState(chatId, States.STOP) != 1) {
                    text = "Something went wrong while updating state\n" +
                            "Please, try again\n";
                } else {
                    state = States.STOP;
                }
            } else if (text.toLowerCase().contains("show all")) {
                text = "";
                List<BotUser> allUsers = userService.getAllUsers();
                for (BotUser currBotUser : allUsers) {
                    text += currBotUser.getChatId().toString() + " " +
                            currBotUser.getOrgId().toString() + " " +
                            currBotUser.getToken() + " " +
                            currBotUser.getStateAsString() + "\n";
                }
            } else if (text.toLowerCase().contains("show me")) {
                text = "";
                Optional<BotUser> oldBotUser = userService.getUserByChatId(chatId);
                if (oldBotUser.isPresent()) {
                    text += oldBotUser.get().getChatId().toString() + " " +
                            oldBotUser.get().getOrgId().toString() + " " +
                            oldBotUser.get().getToken() + " " +
                            oldBotUser.get().getStateAsString() + "\n";
                } else {
                    text = "I have no information about you\n";
                }
            } else if (state.equals(States.STOP) && text.toLowerCase().contains("info")) {
                text = infoMessage;
            } else if (state.equals(States.STOP) && text.toLowerCase().contains("forget me")) {
                if (userService.forgetUser(chatId) == 1) {
                    text = "I erased all information about you\n";
                } else {
                    text = "Something went wrong while erasing your information\n" +
                            "Please, try again\n";
                }
            } else if (state.equals(States.STOP) && text.toLowerCase().contains("start")) {
                text = welcomeMessage;
                Optional<BotUser> oldBotUser = userService.getUserByChatId(chatId);
                if (oldBotUser.isPresent()) {
                    if (oldBotUser.get().getOrgId() != 0) {
                        if (!oldBotUser.get().getToken().isBlank()) {
                            text += "I already have your organisation id and token\n" +
                                    "Tell me what you want to do\n";
                            if (userService.updateUserState(chatId, States.MAIN_MENU) != 1) {
                                text += "Something went wrong while updating state\n" +
                                        "Please, try again\n";
                            } else {
                                state = States.MAIN_MENU;
                            }
                        } else {
                            text += "I already have your organisation id\n" +
                                    "Send me OAuth token for further work\n";
                            if (userService.updateUserState(chatId, States.OAUTH_TOKEN_NEEDED) != 1) {
                                text += "Something went wrong while updating state\n" +
                                        "Please, try again\n";
                            } else {
                                state = States.OAUTH_TOKEN_NEEDED;
                            }
                        }
                    } else {
                        text += "Send me your organisation id for further work\n";
                        if (userService.updateUserState(chatId, States.ORG_ID_NEEDED) != 1) {
                            text += "Something went wrong while updating state\n" +
                                    "Please, try again\n";
                        } else {
                            state = States.ORG_ID_NEEDED;
                        }
                    }
                } else {
                    int addResult = userService.addUser(new BotUser(chatId));
                    if (addResult == 1) {
                        text += "I have successfully saved our chat id\n";
                        text += "Send me your organisation id for further work\n";
                        if (userService.updateUserState(chatId, States.ORG_ID_NEEDED) != 1) {
                            text += "Something went wrong while updating state\n" +
                                    "Please, try again\n";
                        } else {
                            state = States.ORG_ID_NEEDED;
                        }
                    } else {
                        text += "Something went wrong while saving our chat id\n" +
                                "addResult = " + Integer.toString(addResult) + "\n" +
                                "Please try again\n";
                    }
                }
            } else if (state.equals(States.ORG_ID_NEEDED)) {
                int orgId;
                try {
                    orgId = Integer.parseInt(text);
                    int updateResult = userService.updateUserOrgId(chatId, orgId);
                    if (updateResult == 1) {
                        text = "I have successfully saved your organisation id\n";
                        text += "Send me OAuth token for further work\n";
                        if (userService.updateUserState(chatId, States.OAUTH_TOKEN_NEEDED) != 1) {
                            text += "Something went wrong while updating state\n" +
                                    "Please, try again\n";
                        } else {
                            state = States.OAUTH_TOKEN_NEEDED;
                        }
                    } else {
                        text += "Something went wrong while updating organisation id\n" +
                                "updateResult = " + Integer.toString(updateResult) + "\n" +
                                "Please try again\n";
                    }
                } catch (NumberFormatException e) {
                    text = "Please enter only digit form of your organisation id\n";
                }
            } else if (state.equals(States.OAUTH_TOKEN_NEEDED)) {
                String token = text;
                if (!token.isBlank()) {
                    int updateResult = userService.updateUserToken(chatId, token);
                    if (updateResult == 1) {
                        text = "I have successfully saved your token\n";
                        text += "Now tell me what you want to do\n";
                        if (userService.updateUserState(chatId, States.MAIN_MENU) != 1) {
                            text += "Something went wrong while updating state\n" +
                                    "Please, try again\n";
                        } else {
                            state = States.MAIN_MENU;
                        }
                    } else {
                        text += "Something went wrong while saving token\n" +
                                "updateResult = " + Integer.toString(updateResult) + "\n" +
                                "Please try again\n";
                    }
                } else {
                    text = "You need to send not blank token, try again\n";
                }
            } else if (state.equals(States.MAIN_MENU) && text.toLowerCase().contains("update token")) {
                text = "Send me new OAuth token\n";
                if (userService.updateUserState(chatId, States.OAUTH_TOKEN_UPDATE) != 1) {
                    text += "Something went wrong while updating state\n" +
                            "Please, try again\n";
                } else {
                    state = States.OAUTH_TOKEN_UPDATE;
                }
            } else if (state.equals(States.MAIN_MENU) && text.toLowerCase().contains("update organisation id")) {
                text = "Send me new organisation id\n";
                if (userService.updateUserState(chatId, States.ORG_ID_UPDATE) != 1) {
                    text += "Something went wrong while updating state\n" +
                            "Please, try again\n";
                } else {
                    state = States.ORG_ID_UPDATE;
                }
            } else if (state.equals(States.OAUTH_TOKEN_UPDATE)) {
                String token = text;
                if (!token.isBlank()) {
                    int updateResult = userService.updateUserToken(chatId, token);
                    if (updateResult == 1) {
                        text = "I have successfully updated your token\n";
                        if (userService.updateUserState(chatId, States.MAIN_MENU) != 1) {
                            text += "Something went wrong while updating state\n" +
                                    "Please, try again\n";
                        } else {
                            state = States.MAIN_MENU;
                        }
                    } else {
                        text += "Something went wrong while updating token\n" +
                                "updateResult = " + Integer.toString(updateResult) + "\n" +
                                "Please try again\n";
                    }
                } else {
                    text += "You need to send not blank token, try again";
                }
            } else if (state.equals(States.ORG_ID_UPDATE)) {
                int orgId;
                try {
                    orgId = Integer.parseInt(text);
                    int updateResult = userService.updateUserOrgId(chatId, orgId);
                    if (updateResult == 1) {
                        text = "I have successfully updated your organisation id\n";
                        if (userService.updateUserState(chatId, States.MAIN_MENU) != 1) {
                            text += "Something went wrong while updating state\n" +
                                    "Please, try again\n";
                        } else {
                            state = States.MAIN_MENU;
                        }
                    } else {
                        text += "Something went wrong while updating organisation id\n" +
                                "updateResult = " + Integer.toString(updateResult) + "\n" +
                                "Please try again\n";
                        if (userService.updateUserState(chatId, States.MAIN_MENU) != 1) {
                            text += "Something went wrong while updating state\n" +
                                    "Please, try again\n";
                        } else {
                            state = States.MAIN_MENU;
                        }
                    }
                } catch (NumberFormatException e) {
                    text = "Please enter only digit form of your organisation id\n";
                }
            } else if (state.equals(States.MAIN_MENU) && text.toLowerCase().contains("search task by key")) {
                text = "Send me key to search task. It should exactly match the key of needed task\n";
                if (userService.updateUserState(chatId, States.SEARCH_BY_KEY) != 1) {
                    text += "Something went wrong while updating state\n" +
                            "Please, try again\n";
                } else {
                    state = States.SEARCH_BY_KEY;
                }
            } else if (state.equals(States.SEARCH_BY_KEY)) {
                String taskKey = text;
                Optional<BotUser> currBotUser = userService.getUserByChatId(chatId);
                if (currBotUser.isPresent()) {
                    String orgId = currBotUser.get().getOrgId().toString();
                    String token = currBotUser.get().getToken();
                    try {
                        text = TrackerApiHandler.searchByKey(token, orgId, taskKey);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    text = "Something went wrong and I have no information about you\n";
                }
                if (userService.updateUserState(chatId, States.MAIN_MENU) != 1) {
                    text += "Something went wrong while updating state\n" +
                            "Please, try again\n";
                } else {
                    state = States.MAIN_MENU;
                }
            } else {
                text = messageToStart;
                List<BotUser> allUsers = userService.getAllUsers();
                for (BotUser currBotUser : allUsers) {
                    text += currBotUser.getChatId().toString() + " " +
                            currBotUser.getOrgId().toString() + " " +
                            currBotUser.getToken() +  " " +
                            currBotUser.getStateAsString() + "\n";
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

        if (state.equals(States.STOP)) {
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
        } else if (state.equals(States.MAIN_MENU)) {
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(true);

            KeyboardRow keyboardFirstRow = new KeyboardRow();
            keyboardFirstRow.add(new KeyboardButton("Show my tasks"));

            KeyboardRow keyboardSecondRow = new KeyboardRow();
            keyboardSecondRow.add(new KeyboardButton("Search task by key"));

            KeyboardRow keyboardThirdRow = new KeyboardRow();
            keyboardThirdRow.add(new KeyboardButton("Create task"));

            KeyboardRow keyboardFourthRow = new KeyboardRow();
            keyboardThirdRow.add(new KeyboardButton("Update organisation id"));

            KeyboardRow keyboardFifthRow = new KeyboardRow();
            keyboardThirdRow.add(new KeyboardButton("Update token"));


            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);
            keyboard.add(keyboardThirdRow);
        }

        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    @PostConstruct
    public void start() {
        logger.info("username: {}, token: {}", username, token);
    }

}
