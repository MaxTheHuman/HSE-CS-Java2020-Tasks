package ru.hse.cs.java2020.task03.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.hse.cs.java2020.task03.model.BotUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
                " token " +
                ") Values (?, ?, ?)";
        return jdbcTemplate.update(
                sql,
                user.getChatId(),
                user.getOrgId(),
                user.getToken()
        );
    }

    @Override
    public List<BotUser> selectAllUsers() {
        final String sql = "" +
                "Select " +
                " chatId, " +
                " orgId, " +
                " token " +
                "From USERS";

        return jdbcTemplate.query(sql, mapBotUserFromDb());
    }

    @Override
    public Optional<BotUser> selectUserByChatId(Long chatId) {
        final String sql = "" +
                " Select " +
                " chatId, " +
                " orgId, " +
                " token " +
                "From USERS " +
                "Where chatId = ?";

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

            return new BotUser(
                    chatId,
                    orgId,
                    token
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
}
