package com.kalaconnect.repository;

import com.kalaconnect.dto.AdminDashboardMetricsDto;
import com.kalaconnect.model.AdminAction;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

@Repository
public class AdminRepositoryImpl implements AdminRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AdminRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<AdminAction> adminActionRowMapper = (rs, rowNum) -> {
        AdminAction a = new AdminAction();
        a.setId(rs.getLong("id"));
        a.setAdminId(rs.getLong("admin_id"));
        a.setActionType(rs.getString("action_type"));
        a.setTargetType(rs.getString("target_type"));
        a.setTargetId(rs.getLong("target_id"));
        a.setReason(rs.getString("reason"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            a.setCreatedAt(ts.toInstant().atOffset(ZoneOffset.UTC));
        }
        return a;
    };

    @Override
    public AdminDashboardMetricsDto getDashboardMetrics() {
        String sql = """
            SELECT
                (SELECT COUNT(DISTINCT shg_name) FROM artisan_profiles WHERE shg_name IS NOT NULL AND TRIM(shg_name) <> '') as total_shgs,
                (SELECT COUNT(*) FROM artisan_profiles) as total_artisans,
                (SELECT COUNT(*) FROM products) as total_products,
                (SELECT COUNT(*) FROM enquiries) as total_enquiries,
                (SELECT COUNT(*) FROM artisan_profiles WHERE LOWER(approval_status) = 'pending') as pending_approvals,
                (SELECT COUNT(*) FROM artisan_profiles WHERE LOWER(approval_status) = 'approved') as active_artisans,
                (SELECT COUNT(*) FROM enquiries WHERE LOWER(status) = 'pending') as pending_enquiries,
                (SELECT COUNT(*) FROM enquiries WHERE LOWER(status) = 'contacted') as contacted_enquiries,
                (SELECT COUNT(*) FROM enquiries WHERE LOWER(status) IN ('resolved', 'responded')) as resolved_enquiries,
                (SELECT COUNT(*) FROM enquiries WHERE LOWER(status) = 'closed') as closed_enquiries,
                (SELECT COUNT(*) FROM artisan_profiles WHERE LOWER(approval_status) = 'rejected') as rejected_artisans
            """;

        return jdbcTemplate.queryForObject(sql, Collections.emptyMap(), (rs, rowNum) -> {
            AdminDashboardMetricsDto dto = new AdminDashboardMetricsDto();
            dto.setTotalShgs(rs.getLong("total_shgs"));
            dto.setTotalArtisans(rs.getLong("total_artisans"));
            dto.setTotalProducts(rs.getLong("total_products"));
            dto.setTotalEnquiries(rs.getLong("total_enquiries"));
            dto.setPendingApprovals(rs.getLong("pending_approvals"));
            dto.setActiveArtisans(rs.getLong("active_artisans"));

            dto.setPendingEnquiries(rs.getLong("pending_enquiries"));
            dto.setContactedEnquiries(rs.getLong("contacted_enquiries"));
            dto.setResolvedEnquiries(rs.getLong("resolved_enquiries"));
            dto.setClosedEnquiries(rs.getLong("closed_enquiries"));
            dto.setRejectedArtisans(rs.getLong("rejected_artisans"));
            return dto;
        });
    }

    @Override
    public void recordAdminAction(AdminAction action) {
        String sql = """
            INSERT INTO admin_actions (admin_id, action_type, target_type, target_id, reason, created_at)
            VALUES (:adminId, :actionType, :targetType, :targetId, :reason, :createdAt)
            """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("adminId", action.getAdminId())
                .addValue("actionType", action.getActionType())
                .addValue("targetType", action.getTargetType())
                .addValue("targetId", action.getTargetId())
                .addValue("reason", action.getReason())
                .addValue("createdAt", action.getCreatedAt() != null ? action.getCreatedAt() : OffsetDateTime.now());

        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<AdminAction> findRecentActions(int limit) {
        String sql = "SELECT * FROM admin_actions ORDER BY created_at DESC LIMIT :limit";
        MapSqlParameterSource params = new MapSqlParameterSource("limit", limit > 0 ? limit : 20);
        return jdbcTemplate.query(sql, params, adminActionRowMapper);
    }
}
