package ru.hse.cs.java2020.task03.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.hse.cs.java2020.task03.common.States;
import ru.hse.cs.java2020.task03.model.BotUser;

import java.util.List;
import java.util.Optional;

@Repository("postgres")
public class BotUserDataAccessService implements BotUserDao{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BotUserDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int insertUser(BotUser user) {
        final String sql = "" +
                "Insert Into USERS (" +
                " chatId, " +
                " orgId, " +
                " token, " +
                " state, " +
                " page " +
                ") Values (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(
                sql,
                user.getChatId(),
                user.getOrgId(),
                user.getToken(),
                user.getStateAsString(),
                user.getPage()
        );
    }

    @Override
    public List<BotUser> selectAllUsers() {
        final String sql = "" +
                "Select " +
                " chatId, " +
                " orgId, " +
                " token, " +
                " state " +
                "From USERS";

        return jdbcTemplate.query(sql, mapBotUserFromDb());
    }

    @Override
    public Optional<BotUser> selectUserByChatId(Long chatId) {
        final String sql = "" +
                " Select " +
                " chatId, " +
                " orgId, " +
                " token, " +
                " state, " +
                " page " +
                "From USERS " +
                "Where chatId = " +
                chatId.toString();

        List<BotUser> botUsers = jdbcTemplate.query(sql, mapBotUserFromDb());
        BotUser botUser = null;
        if (!botUsers.isEmpty()) {
            botUser = botUsers.get(0);
        }

        return Optional.ofNullable(botUser);
    }

    private RowMapper<BotUser> mapBotUserFromDb() {
        return (resultSet, i) -> {
            String chatIdStr = resultSet.getString("chatId");
            Long chatId = Long.parseLong(chatIdStr);

            String orgIdStr = resultSet.getString("orgId");
            Integer orgId = Integer.parseInt(orgIdStr);

            String token = resultSet.getString("token");

            String stateStr = resultSet.getString("state");
            States state;
            try {
                state = States.valueOf(stateStr);
            } catch (IllegalArgumentException e) {
                state = States.ILLEGAL_STATE;
            }

            int page = resultSet.getInt("page");

            return new BotUser(
                    chatId,
                    orgId,
                    token,
                    state,
                    page
            );
        };
    }

    @Override
    public int deleteUserByChatId(Long chatId) {
        String sql = "" +
                "Delete From USERS " +
                "Where chatId = ?";
        return jdbcTemplate.update(sql, chatId);
    }

    @Override
    public int updateUserOrgId(Long chatId, Integer orgId) {
        String sql = "" +
                "Update USERS " +
                "Set orgId = ? " +
                "Where chatId = ?";
        return jdbcTemplate.update(sql, orgId, chatId);
    }

    @Override
    public int updateUserToken(Long chatId, String token) {
        String sql = "" +
                "Update USERS " +
                "Set token = ? " +
                "Where chatId = ?";
        return jdbcTemplate.update(sql, token, chatId);
    }

    @Override
    public int updateUserState(Long chatId, States state) {
        String sql = "" +
                "Update USERS " +
                "Set state = ? " +
                "Where chatId = ?";
        return jdbcTemplate.update(sql, state.toString(), chatId);
    }

    @Override
    public int updateUserPage(Long chatId, int page) {
        String sql = "" +
                "Update USERS " +
                "Set state = ? " +
                "Where chatId = ?";
        return jdbcTemplate.update(sql, page, chatId);
    }
}
