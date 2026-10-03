package com.kalaconnect.repository;

import com.kalaconnect.model.Notification;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public NotificationRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Notification> notificationRowMapper = (rs, rowNum) -> {
        Notification n = new Notification();
        n.setId(rs.getLong("id"));
        n.setUserId(rs.getLong("user_id"));
        n.setTitle(rs.getString("title"));
        n.setMessage(rs.getString("message"));
        n.setType(rs.getString("type"));
        n.setRead(rs.getBoolean("is_read"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            n.setCreatedAt(createdAt.toInstant().atOffset(ZoneOffset.UTC));
        }
        return n;
    };

    @Override
    public Notification save(Notification notification) {
        if (notification.getId() == null) {
            String sql = """
                INSERT INTO notifications (user_id, title, message, type, is_read, created_at)
                VALUES (:userId, :title, :message, :type, :isRead, :createdAt)
                """;
            KeyHolder keyHolder = new GeneratedKeyHolder();
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("userId", notification.getUserId())
                    .addValue("title", notification.getTitle())
                    .addValue("message", notification.getMessage())
                    .addValue("type", notification.getType())
                    .addValue("isRead", notification.isRead())
                    .addValue("createdAt", OffsetDateTime.now());

            jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
            Number id = keyHolder.getKey();
            if (id != null) {
                notification.setId(id.longValue());
            }
            return notification;
        } else {
            String sql = "UPDATE notifications SET is_read = :isRead WHERE id = :id";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", notification.getId())
                    .addValue("isRead", notification.isRead());
            jdbcTemplate.update(sql, params);
            return notification;
        }
    }

    @Override
    public Optional<Notification> findById(Long id) {
        String sql = "SELECT * FROM notifications WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, params, notificationRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Notification> findByUserId(Long userId, Boolean unreadOnly) {
        StringBuilder sql = new StringBuilder("SELECT * FROM notifications WHERE user_id = :userId");
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);

        if (Boolean.TRUE.equals(unreadOnly)) {
            sql.append(" AND is_read = FALSE");
        }
        sql.append(" ORDER BY created_at DESC");

        return jdbcTemplate.query(sql.toString(), params, notificationRowMapper);
    }

    @Override
    public void markAsRead(Long id, Long userId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = :id AND user_id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("userId", userId);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void markAllAsRead(Long userId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE user_id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public int countUnread(Long userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = :userId AND is_read = FALSE";
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        Integer count = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null ? count : 0;
    }
}
