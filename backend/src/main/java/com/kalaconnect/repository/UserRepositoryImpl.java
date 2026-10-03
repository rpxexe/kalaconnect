package com.kalaconnect.repository;

import com.kalaconnect.model.ArtisanProfile;
import com.kalaconnect.model.NgoProfile;
import com.kalaconnect.model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper = new UserRowMapper();

    public UserRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            String sql = """
                INSERT INTO users (name, email, phone, password_hash, role, profile_image, language, is_verified, created_at, updated_at)
                VALUES (:name, :email, :phone, :passwordHash, :role, :profileImage, :language, :isVerified, :createdAt, :updatedAt)
                """;

            KeyHolder keyHolder = new GeneratedKeyHolder();
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("name", user.getName())
                    .addValue("email", user.getEmail().toLowerCase().trim())
                    .addValue("phone", user.getPhone())
                    .addValue("passwordHash", user.getPasswordHash())
                    .addValue("role", user.getRole().name())
                    .addValue("profileImage", user.getProfileImage())
                    .addValue("language", user.getLanguage() != null ? user.getLanguage() : "en")
                    .addValue("isVerified", user.isVerified())
                    .addValue("createdAt", OffsetDateTime.now())
                    .addValue("updatedAt", OffsetDateTime.now());

            jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
            Number generatedId = keyHolder.getKey();
            if (generatedId != null) {
                user.setId(generatedId.longValue());
            }
            return user;
        } else {
            String sql = """
                UPDATE users
                SET name = :name, phone = :phone, profile_image = :profileImage,
                    language = :language, is_verified = :isVerified, updated_at = :updatedAt
                WHERE id = :id
                """;
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", user.getId())
                    .addValue("name", user.getName())
                    .addValue("phone", user.getPhone())
                    .addValue("profileImage", user.getProfileImage())
                    .addValue("language", user.getLanguage() != null ? user.getLanguage() : "en")
                    .addValue("isVerified", user.isVerified())
                    .addValue("updatedAt", OffsetDateTime.now());

            jdbcTemplate.update(sql, params);
            return user;
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            User user = jdbcTemplate.queryForObject(sql, params, userRowMapper);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE LOWER(email) = :email";
        MapSqlParameterSource params = new MapSqlParameterSource("email", email.toLowerCase().trim());
        try {
            User user = jdbcTemplate.queryForObject(sql, params, userRowMapper);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(email) = :email";
        MapSqlParameterSource params = new MapSqlParameterSource("email", email.toLowerCase().trim());
        Integer count = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public void createArtisanProfile(ArtisanProfile profile) {
        String sql = """
            INSERT INTO artisan_profiles (user_id, shg_name, artisan_name, bio, location, district, state, experience, approval_status, created_at, updated_at)
            VALUES (:userId, :shgName, :artisanName, :bio, :location, :district, :state, :experience, :approvalStatus, :createdAt, :updatedAt)
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", profile.getUserId())
                .addValue("shgName", profile.getShgName())
                .addValue("artisanName", profile.getArtisanName() != null ? profile.getArtisanName() : "Artisan")
                .addValue("bio", profile.getBio())
                .addValue("location", profile.getLocation())
                .addValue("district", profile.getDistrict())
                .addValue("state", profile.getState())
                .addValue("experience", profile.getExperience() != null ? profile.getExperience() : 0)
                .addValue("approvalStatus", profile.getApprovalStatus() != null ? profile.getApprovalStatus() : "PENDING")
                .addValue("createdAt", OffsetDateTime.now())
                .addValue("updatedAt", OffsetDateTime.now());

        jdbcTemplate.update(sql, params);
    }

    @Override
    public void createNgoProfile(NgoProfile profile) {
        // Handled if ngo_profiles table exists
        String sql = """
            INSERT INTO admin_actions (admin_id, action_type, target_type, target_id, reason, created_at)
            VALUES (:adminId, 'REGISTER_NGO', 'USER', :adminId, :orgName, :createdAt)
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("adminId", profile.getUserId())
                .addValue("orgName", profile.getOrganizationName())
                .addValue("createdAt", OffsetDateTime.now());

        jdbcTemplate.update(sql, params);
    }
}
