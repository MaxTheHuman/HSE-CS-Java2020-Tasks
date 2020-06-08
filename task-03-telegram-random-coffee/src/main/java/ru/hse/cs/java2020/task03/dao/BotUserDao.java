package ru.hse.cs.java2020.task03.dao;

import ru.hse.cs.java2020.task03.common.States;
import ru.hse.cs.java2020.task03.model.BotUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BotUserDao {
    int insertUser(BotUser user);

    List<BotUser> selectAllUsers();

    Optional<BotUser> selectUserByChatId(Long chatId);

    int deleteUserByChatId(Long chatId);

    int updateUserOrgId(Long chatId, Integer orgId);

    int updateUserToken(Long chatId, String token);

    int updateUserState(Long chatId, States state);

    int updateUserPage(Long chatId, int page);
}
