package ru.hse.cs.java2020.task03.service;

import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.hse.cs.java2020.task03.common.States;
import ru.hse.cs.java2020.task03.dao.BotUserDao;
import ru.hse.cs.java2020.task03.model.BotUser;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final BotUserDao botUserDao;

    public UserService(@Qualifier("postgres") BotUserDao botUserDao) {
        this.botUserDao = botUserDao;
    }

    public int addUser(BotUser user) {
//        int result;
//        try {
//            result = botUserDao.insertUser(user);
//        } catch (PSQLException e) {
//            result = 1;
//        }
        return botUserDao.insertUser(user);
    }

    public List<BotUser> getAllUsers() {
        return botUserDao.selectAllUsers();
    }

    public Optional<BotUser> getUserByChatId(Long chatId) {
        return botUserDao.selectUserByChatId(chatId);
    }

    public int updateUserOrgId(Long chatId, Integer orgId) {
        return botUserDao.updateUserOrgId(chatId, orgId);
    }

    public int updateUserToken(Long chatId, String token) {
        return botUserDao.updateUserToken(chatId, token);
    }

    public int updateUserState(Long chatId, States state) {
        return botUserDao.updateUserState(chatId, state);
    }

    public int forgetUser(Long chatId) {
        return botUserDao.deleteUserByChatId(chatId);
    }

}
