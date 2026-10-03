package com.kalaconnect.repository;

import com.kalaconnect.model.ArtisanProfile;
import com.kalaconnect.model.ArtisanSkill;
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
public class ArtisanRepositoryImpl implements ArtisanRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ArtisanRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ArtisanProfile> artisanProfileRowMapper = (rs, rowNum) -> {
        ArtisanProfile p = new ArtisanProfile();
        p.setId(rs.getLong("id"));
        p.setUserId(rs.getLong("user_id"));
        p.setShgName(rs.getString("shg_name"));
        p.setArtisanName(rs.getString("artisan_name"));
        p.setBio(rs.getString("bio"));
        p.setLocation(rs.getString("location"));
        p.setDistrict(rs.getString("district"));
        p.setState(rs.getString("state"));
        p.setExperience(rs.getInt("experience"));
        p.setApprovalStatus(rs.getString("approval_status"));

        try {
            p.setContactPreference(rs.getString("contact_preference"));
        } catch (Exception ignored) {}

        try {
            p.setProfileImage(rs.getString("profile_image"));
        } catch (Exception ignored) {}

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            p.setCreatedAt(createdAt.toInstant().atOffset(ZoneOffset.UTC));
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            p.setUpdatedAt(updatedAt.toInstant().atOffset(ZoneOffset.UTC));
        }
        return p;
    };

    private final RowMapper<ArtisanSkill> artisanSkillRowMapper = (rs, rowNum) -> {
        ArtisanSkill s = new ArtisanSkill();
        s.setId(rs.getLong("id"));
        s.setArtisanProfileId(rs.getLong("artisan_profile_id"));
        s.setArtisanId(rs.getLong("artisan_id"));
        s.setSkillId(rs.getLong("skill_id"));
        s.setSkillName(rs.getString("skill_name"));
        s.setProficiencyLevel(rs.getString("proficiency_level"));
        s.setYearsOfExperience(rs.getInt("years_of_experience"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            s.setCreatedAt(createdAt.toInstant().atOffset(ZoneOffset.UTC));
        }
        return s;
    };

    private final RowMapper<com.kalaconnect.model.Skill> skillRowMapper = (rs, rowNum) -> {
        com.kalaconnect.model.Skill sk = new com.kalaconnect.model.Skill();
        sk.setId(rs.getLong("id"));
        sk.setName(rs.getString("name"));
        sk.setCategory(rs.getString("category"));
        sk.setDescription(rs.getString("description"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            sk.setCreatedAt(createdAt.toInstant().atOffset(ZoneOffset.UTC));
        }
        return sk;
    };

    @Override
    public ArtisanProfile save(ArtisanProfile profile) {
        if (profile.getId() == null) {
            String sql = """
                INSERT INTO artisan_profiles (user_id, shg_name, artisan_name, bio, location, district, state, experience, contact_preference, profile_image, approval_status, created_at, updated_at)
                VALUES (:userId, :shgName, :artisanName, :bio, :location, :district, :state, :experience, :contactPreference, :profileImage, :approvalStatus, :createdAt, :updatedAt)
                """;
            KeyHolder keyHolder = new GeneratedKeyHolder();
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("userId", profile.getUserId())
                    .addValue("shgName", profile.getShgName())
                    .addValue("artisanName", profile.getArtisanName())
                    .addValue("bio", profile.getBio())
                    .addValue("location", profile.getLocation())
                    .addValue("district", profile.getDistrict())
                    .addValue("state", profile.getState())
                    .addValue("experience", profile.getExperience() != null ? profile.getExperience() : 0)
                    .addValue("contactPreference", profile.getContactPreference() != null ? profile.getContactPreference() : "PHONE")
                    .addValue("profileImage", profile.getProfileImage())
                    .addValue("approvalStatus", profile.getApprovalStatus() != null ? profile.getApprovalStatus() : "PENDING")
                    .addValue("createdAt", OffsetDateTime.now())
                    .addValue("updatedAt", OffsetDateTime.now());

            jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
            Number id = keyHolder.getKey();
            if (id != null) {
                profile.setId(id.longValue());
            }
        } else {
            String sql = """
                UPDATE artisan_profiles
                SET shg_name = :shgName, artisan_name = :artisanName, bio = :bio, location = :location,
                    district = :district, state = :state, experience = :experience, contact_preference = :contactPreference,
                    profile_image = :profileImage, approval_status = :approvalStatus, updated_at = :updatedAt
                WHERE id = :id
                """;
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", profile.getId())
                    .addValue("shgName", profile.getShgName())
                    .addValue("artisanName", profile.getArtisanName())
                    .addValue("bio", profile.getBio())
                    .addValue("location", profile.getLocation())
                    .addValue("district", profile.getDistrict())
                    .addValue("state", profile.getState())
                    .addValue("experience", profile.getExperience())
                    .addValue("contactPreference", profile.getContactPreference())
                    .addValue("profileImage", profile.getProfileImage())
                    .addValue("approvalStatus", profile.getApprovalStatus())
                    .addValue("updatedAt", OffsetDateTime.now());

            jdbcTemplate.update(sql, params);
        }

        if (profile.getSkills() != null && profile.getUserId() != null) {
            syncArtisanSkills(profile.getId(), profile.getUserId(), profile.getSkills());
        }

        enrichProfile(profile);
        return profile;
    }

    @Override
    public Optional<ArtisanProfile> findById(Long id) {
        String sql = "SELECT * FROM artisan_profiles WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            ArtisanProfile p = jdbcTemplate.queryForObject(sql, params, artisanProfileRowMapper);
            if (p != null) {
                enrichProfile(p);
            }
            return Optional.ofNullable(p);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ArtisanProfile> findByUserId(Long userId) {
        String sql = "SELECT * FROM artisan_profiles WHERE user_id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        try {
            ArtisanProfile p = jdbcTemplate.queryForObject(sql, params, artisanProfileRowMapper);
            if (p != null) {
                enrichProfile(p);
            }
            return Optional.ofNullable(p);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<ArtisanProfile> findAll(String district, String state, String approvalStatus) {
        StringBuilder sql = new StringBuilder("SELECT * FROM artisan_profiles WHERE 1=1 ");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (district != null && !district.isBlank()) {
            sql.append("AND LOWER(district) = LOWER(:district) ");
            params.addValue("district", district.trim());
        }
        if (state != null && !state.isBlank()) {
            sql.append("AND LOWER(state) = LOWER(:state) ");
            params.addValue("state", state.trim());
        }
        if (approvalStatus != null && !approvalStatus.isBlank()) {
            sql.append("AND approval_status = :approvalStatus ");
            params.addValue("approvalStatus", approvalStatus.trim());
        }
        sql.append("ORDER BY created_at DESC");

        return jdbcTemplate.query(sql.toString(), params, artisanProfileRowMapper);
    }

    @Override
    public void updateApprovalStatus(Long id, String status) {
        String sql = "UPDATE artisan_profiles SET approval_status = :status, updated_at = :updatedAt WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("status", status)
                .addValue("updatedAt", OffsetDateTime.now());
        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<ArtisanSkill> findSkillsByArtisanId(Long artisanId) {
        String sql = """
            SELECT ask.*, s.name as skill_name
            FROM artisan_skills ask
            JOIN skills s ON ask.skill_id = s.id
            WHERE ask.artisan_id = :artisanId
            ORDER BY ask.created_at ASC
            """;
        MapSqlParameterSource params = new MapSqlParameterSource("artisanId", artisanId);
        return jdbcTemplate.query(sql, params, artisanSkillRowMapper);
    }

    @Override
    public List<String> findSkillNamesByArtisanId(Long artisanId) {
        String sql = """
            SELECT s.name
            FROM artisan_skills ask
            JOIN skills s ON ask.skill_id = s.id
            WHERE ask.artisan_id = :artisanId
            ORDER BY s.name ASC
            """;
        MapSqlParameterSource params = new MapSqlParameterSource("artisanId", artisanId);
        return jdbcTemplate.query(sql, params, (rs, rowNum) -> rs.getString("name"));
    }

    @Override
    public void syncArtisanSkills(Long artisanProfileId, Long artisanId, List<String> skillNames) {
        if (artisanId == null) return;
        String deleteSql = "DELETE FROM artisan_skills WHERE artisan_id = :artisanId";
        jdbcTemplate.update(deleteSql, new MapSqlParameterSource("artisanId", artisanId));

        if (skillNames == null || skillNames.isEmpty()) {
            return;
        }

        for (String skillName : skillNames) {
            if (skillName == null || skillName.trim().isEmpty()) continue;
            String trimmedName = skillName.trim();
            Long skillId = null;
            try {
                skillId = jdbcTemplate.queryForObject(
                        "SELECT id FROM skills WHERE LOWER(name) = LOWER(:name)",
                        new MapSqlParameterSource("name", trimmedName),
                        Long.class
                );
            } catch (EmptyResultDataAccessException e) {
                KeyHolder kh = new GeneratedKeyHolder();
                jdbcTemplate.update(
                        "INSERT INTO skills (name, category, created_at) VALUES (:name, 'Handicraft', :createdAt)",
                        new MapSqlParameterSource().addValue("name", trimmedName).addValue("createdAt", OffsetDateTime.now()),
                        kh,
                        new String[]{"id"}
                );
                Number num = kh.getKey();
                if (num != null) {
                    skillId = num.longValue();
                }
            }

            if (skillId != null) {
                String insertArtisanSkill = """
                    INSERT INTO artisan_skills (artisan_profile_id, artisan_id, skill_id, proficiency_level, years_of_experience, created_at)
                    VALUES (:artisanProfileId, :artisanId, :skillId, 'INTERMEDIATE', 0, :createdAt)
                    ON CONFLICT (artisan_id, skill_id) DO NOTHING
                    """;
                MapSqlParameterSource p = new MapSqlParameterSource()
                        .addValue("artisanProfileId", artisanProfileId != null ? artisanProfileId : 0L)
                        .addValue("artisanId", artisanId)
                        .addValue("skillId", skillId)
                        .addValue("createdAt", OffsetDateTime.now());
                jdbcTemplate.update(insertArtisanSkill, p);
            }
        }
    }

    @Override
    public void addSkillToArtisan(ArtisanSkill skill) {
        String sql = """
            INSERT INTO artisan_skills (artisan_profile_id, artisan_id, skill_id, proficiency_level, years_of_experience, created_at)
            VALUES (:artisanProfileId, :artisanId, :skillId, :proficiencyLevel, :yearsOfExperience, :createdAt)
            ON CONFLICT (artisan_id, skill_id) DO UPDATE
            SET proficiency_level = EXCLUDED.proficiency_level,
                years_of_experience = EXCLUDED.years_of_experience
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("artisanProfileId", skill.getArtisanProfileId())
                .addValue("artisanId", skill.getArtisanId())
                .addValue("skillId", skill.getSkillId())
                .addValue("proficiencyLevel", skill.getProficiencyLevel())
                .addValue("yearsOfExperience", skill.getYearsOfExperience())
                .addValue("createdAt", OffsetDateTime.now());
        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<com.kalaconnect.model.Skill> findAllSkills() {
        String sql = "SELECT * FROM skills ORDER BY name ASC";
        return jdbcTemplate.query(sql, skillRowMapper);
    }

    @Override
    public List<ArtisanProfile> searchArtisansWithFilters(
            String searchQuery,
            String craftCategory,
            String skill,
            String state,
            String district,
            String approvalStatus,
            int limit,
            int offset) {

        StringBuilder sql = new StringBuilder("""
            SELECT DISTINCT ap.* FROM artisan_profiles ap
            LEFT JOIN users u ON u.id = ap.user_id
            LEFT JOIN artisan_skills ask ON (ask.artisan_profile_id = ap.id OR ask.artisan_id = ap.user_id)
            LEFT JOIN skills s ON s.id = ask.skill_id
            WHERE 1=1
            """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        buildArtisanFilterClauses(sql, params, searchQuery, craftCategory, skill, state, district, approvalStatus);

        sql.append(" ORDER BY ap.created_at DESC LIMIT :limit OFFSET :offset");
        params.addValue("limit", limit > 0 ? limit : 20);
        params.addValue("offset", Math.max(offset, 0));

        List<ArtisanProfile> profiles = jdbcTemplate.query(sql.toString(), params, artisanProfileRowMapper);
        for (ArtisanProfile profile : profiles) {
            enrichProfile(profile);
        }
        return profiles;
    }

    @Override
    public long countArtisansWithFilters(
            String searchQuery,
            String craftCategory,
            String skill,
            String state,
            String district,
            String approvalStatus) {

        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(DISTINCT ap.id) FROM artisan_profiles ap
            LEFT JOIN users u ON u.id = ap.user_id
            LEFT JOIN artisan_skills ask ON (ask.artisan_profile_id = ap.id OR ask.artisan_id = ap.user_id)
            LEFT JOIN skills s ON s.id = ask.skill_id
            WHERE 1=1
            """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        buildArtisanFilterClauses(sql, params, searchQuery, craftCategory, skill, state, district, approvalStatus);

        Long count = jdbcTemplate.queryForObject(sql.toString(), params, Long.class);
        return count != null ? count : 0L;
    }

    private void buildArtisanFilterClauses(
            StringBuilder sql,
            MapSqlParameterSource params,
            String searchQuery,
            String craftCategory,
            String skill,
            String state,
            String district,
            String approvalStatus) {

        if (approvalStatus != null && !approvalStatus.isBlank() && !approvalStatus.equalsIgnoreCase("ALL")) {
            sql.append(" AND LOWER(ap.approval_status) = LOWER(:approvalStatus)");
            params.addValue("approvalStatus", approvalStatus.trim());
        }

        if (state != null && !state.isBlank() && !state.equalsIgnoreCase("ALL")) {
            sql.append(" AND (LOWER(ap.state) = LOWER(:state) OR LOWER(ap.location) LIKE :stateLike)");
            params.addValue("state", state.trim());
            params.addValue("stateLike", "%" + state.trim().toLowerCase() + "%");
        }

        if (district != null && !district.isBlank() && !district.equalsIgnoreCase("ALL")) {
            sql.append(" AND (LOWER(ap.district) = LOWER(:district) OR LOWER(ap.location) LIKE :districtLike)");
            params.addValue("district", district.trim());
            params.addValue("districtLike", "%" + district.trim().toLowerCase() + "%");
        }

        if (craftCategory != null && !craftCategory.isBlank() && !craftCategory.equalsIgnoreCase("ALL")) {
            sql.append(" AND (LOWER(s.category) LIKE :craftCatLike OR LOWER(s.name) LIKE :craftCatLike)");
            params.addValue("craftCatLike", "%" + craftCategory.trim().toLowerCase() + "%");
        }

        if (skill != null && !skill.isBlank() && !skill.equalsIgnoreCase("ALL")) {
            sql.append(" AND LOWER(s.name) LIKE :skillLike");
            params.addValue("skillLike", "%" + skill.trim().toLowerCase() + "%");
        }

        if (searchQuery != null && !searchQuery.isBlank()) {
            String q = "%" + searchQuery.trim().toLowerCase() + "%";
            sql.append(" AND (LOWER(ap.artisan_name) LIKE :q OR LOWER(u.name) LIKE :q OR LOWER(ap.shg_name) LIKE :q " +
                    "OR LOWER(ap.bio) LIKE :q OR LOWER(ap.location) LIKE :q OR LOWER(s.name) LIKE :q)");
            params.addValue("q", q);
        }
    }

    private void enrichProfile(ArtisanProfile p) {
        if (p == null) return;
        List<String> skills = findSkillNamesByArtisanId(p.getUserId());
        p.setSkills(skills);
        p.setCompletionPercentage(calculateCompletionPercentage(p, skills));
    }

    private int calculateCompletionPercentage(ArtisanProfile p, List<String> skills) {
        int score = 0;
        if (p.getArtisanName() != null && !p.getArtisanName().trim().isEmpty()) score += 10;
        if (p.getShgName() != null && !p.getShgName().trim().isEmpty()) score += 10;
        if (p.getBio() != null && !p.getBio().trim().isEmpty()) score += 10;
        if (p.getProfileImage() != null && !p.getProfileImage().trim().isEmpty()) score += 10;
        if (p.getLocation() != null && !p.getLocation().trim().isEmpty()) score += 10;
        if (p.getDistrict() != null && !p.getDistrict().trim().isEmpty()) score += 10;
        if (p.getState() != null && !p.getState().trim().isEmpty()) score += 10;
        if (p.getExperience() != null && p.getExperience() > 0) score += 10;
        if (skills != null && !skills.isEmpty()) score += 10;
        if (p.getContactPreference() != null && !p.getContactPreference().trim().isEmpty()) score += 10;
        return Math.min(score, 100);
    }
}
