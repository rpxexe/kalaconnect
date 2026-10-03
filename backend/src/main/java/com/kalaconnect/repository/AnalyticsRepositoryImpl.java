package com.kalaconnect.repository;

import com.kalaconnect.dto.AnalyticsMetricItemDto;
import com.kalaconnect.dto.InactiveArtisanDto;
import com.kalaconnect.dto.LowViewProductDto;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class AnalyticsRepositoryImpl implements AnalyticsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AnalyticsRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long countTotalArtisans() {
        String sql = "SELECT COUNT(*) FROM artisan_profiles";
        Long count = jdbcTemplate.queryForObject(sql, Collections.emptyMap(), Long.class);
        return count != null ? count : 0L;
    }

    @Override
    public long countTotalShgs() {
        String sql = "SELECT COUNT(DISTINCT shg_name) FROM artisan_profiles WHERE shg_name IS NOT NULL AND TRIM(shg_name) <> ''";
        Long count = jdbcTemplate.queryForObject(sql, Collections.emptyMap(), Long.class);
        return count != null ? count : 0L;
    }

    @Override
    public long countTotalProducts() {
        String sql = "SELECT COUNT(*) FROM products";
        Long count = jdbcTemplate.queryForObject(sql, Collections.emptyMap(), Long.class);
        return count != null ? count : 0L;
    }

    @Override
    public long countTotalEnquiries() {
        String sql = "SELECT COUNT(*) FROM enquiries";
        Long count = jdbcTemplate.queryForObject(sql, Collections.emptyMap(), Long.class);
        return count != null ? count : 0L;
    }

    @Override
    public List<AnalyticsMetricItemDto> getProductsPerDistrict(int limit) {
        String sql = """
            SELECT COALESCE(NULLIF(TRIM(ap.district), ''), 'Unspecified') AS label,
                   COUNT(p.id) AS count
            FROM products p
            LEFT JOIN artisan_profiles ap ON ap.user_id = p.artisan_id
            GROUP BY COALESCE(NULLIF(TRIM(ap.district), ''), 'Unspecified')
            ORDER BY count DESC
            LIMIT :limit
            """;
        return jdbcTemplate.query(sql, new MapSqlParameterSource("limit", Math.max(limit, 1)),
                (rs, rowNum) -> new AnalyticsMetricItemDto(rs.getString("label"), rs.getLong("count")));
    }

    @Override
    public List<AnalyticsMetricItemDto> getArtisansPerDistrict(int limit) {
        String sql = """
            SELECT COALESCE(NULLIF(TRIM(district), ''), 'Unspecified') AS label,
                   COUNT(*) AS count
            FROM artisan_profiles
            GROUP BY COALESCE(NULLIF(TRIM(district), ''), 'Unspecified')
            ORDER BY count DESC
            LIMIT :limit
            """;
        return jdbcTemplate.query(sql, new MapSqlParameterSource("limit", Math.max(limit, 1)),
                (rs, rowNum) -> new AnalyticsMetricItemDto(rs.getString("label"), rs.getLong("count")));
    }

    @Override
    public List<AnalyticsMetricItemDto> getProductsByCraftCategory(int limit) {
        String sql = """
            SELECT category AS label,
                   COUNT(*) AS count
            FROM products
            GROUP BY category
            ORDER BY count DESC
            LIMIT :limit
            """;
        return jdbcTemplate.query(sql, new MapSqlParameterSource("limit", Math.max(limit, 1)),
                (rs, rowNum) -> new AnalyticsMetricItemDto(rs.getString("label"), rs.getLong("count")));
    }

    @Override
    public List<AnalyticsMetricItemDto> getEnquiriesByProduct(int limit) {
        String sql = """
            SELECT p.name AS label,
                   COUNT(e.id) AS count
            FROM enquiries e
            JOIN products p ON e.product_id = p.id
            GROUP BY p.name
            ORDER BY count DESC
            LIMIT :limit
            """;
        return jdbcTemplate.query(sql, new MapSqlParameterSource("limit", Math.max(limit, 1)),
                (rs, rowNum) -> new AnalyticsMetricItemDto(rs.getString("label"), rs.getLong("count")));
    }

    @Override
    public List<AnalyticsMetricItemDto> getEnquiriesByArtisan(int limit) {
        String sql = """
            SELECT COALESCE(ap.artisan_name, u.name, 'Artisan') AS label,
                   COUNT(e.id) AS count
            FROM enquiries e
            JOIN users u ON e.artisan_id = u.id
            LEFT JOIN artisan_profiles ap ON ap.user_id = u.id
            GROUP BY COALESCE(ap.artisan_name, u.name, 'Artisan')
            ORDER BY count DESC
            LIMIT :limit
            """;
        return jdbcTemplate.query(sql, new MapSqlParameterSource("limit", Math.max(limit, 1)),
                (rs, rowNum) -> new AnalyticsMetricItemDto(rs.getString("label"), rs.getLong("count")));
    }

    @Override
    public List<LowViewProductDto> getProductsWithLowViews(int limit) {
        String sql = """
            SELECT p.id,
                   p.name,
                   p.category,
                   COALESCE(ap.artisan_name, u.name, 'Artisan') AS artisan_name,
                   COALESCE(p.views_count, 0) AS views
            FROM products p
            LEFT JOIN users u ON u.id = p.artisan_id
            LEFT JOIN artisan_profiles ap ON ap.user_id = p.artisan_id
            ORDER BY p.views_count ASC, p.created_at DESC
            LIMIT :limit
            """;
        return jdbcTemplate.query(sql, new MapSqlParameterSource("limit", Math.max(limit, 1)),
                (rs, rowNum) -> new LowViewProductDto(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("artisan_name"),
                        rs.getInt("views")
                ));
    }

    @Override
    public List<InactiveArtisanDto> getInactiveArtisans(int limit) {
        String sql = """
            SELECT ap.id,
                   ap.artisan_name,
                   COALESCE(ap.shg_name, 'Independent') AS shg_name,
                   COALESCE(ap.district, 'Unspecified') AS district,
                   COUNT(p.id) AS product_count
            FROM artisan_profiles ap
            LEFT JOIN products p ON p.artisan_id = ap.user_id
            GROUP BY ap.id, ap.artisan_name, ap.shg_name, ap.district
            HAVING COUNT(p.id) = 0
            ORDER BY ap.created_at DESC
            LIMIT :limit
            """;
        return jdbcTemplate.query(sql, new MapSqlParameterSource("limit", Math.max(limit, 1)),
                (rs, rowNum) -> new InactiveArtisanDto(
                        rs.getLong("id"),
                        rs.getString("artisan_name"),
                        rs.getString("shg_name"),
                        rs.getString("district"),
                        rs.getInt("product_count")
                ));
    }

    @Override
    public List<AnalyticsMetricItemDto> getDistrictsWithLowParticipation(int limit) {
        String sql = """
            SELECT COALESCE(NULLIF(TRIM(district), ''), 'Unspecified') AS label,
                   COUNT(*) AS count
            FROM artisan_profiles
            GROUP BY COALESCE(NULLIF(TRIM(district), ''), 'Unspecified')
            HAVING COUNT(*) <= 2
            ORDER BY count ASC
            LIMIT :limit
            """;
        return jdbcTemplate.query(sql, new MapSqlParameterSource("limit", Math.max(limit, 1)),
                (rs, rowNum) -> new AnalyticsMetricItemDto(rs.getString("label"), rs.getLong("count")));
    }

    @Override
    public List<AnalyticsMetricItemDto> getSkillsWithLowRepresentation(int limit) {
        String sql = """
            SELECT s.name AS label,
                   COUNT(ask.id) AS count
            FROM skills s
            LEFT JOIN artisan_skills ask ON s.id = ask.skill_id
            GROUP BY s.name
            ORDER BY count ASC, s.name ASC
            LIMIT :limit
            """;
        return jdbcTemplate.query(sql, new MapSqlParameterSource("limit", Math.max(limit, 1)),
                (rs, rowNum) -> new AnalyticsMetricItemDto(rs.getString("label"), rs.getLong("count")));
    }
}
