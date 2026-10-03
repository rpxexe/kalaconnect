package com.kalaconnect.repository;

import com.kalaconnect.model.Enquiry;
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
public class EnquiryRepositoryImpl implements EnquiryRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public EnquiryRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Enquiry> enquiryRowMapper = (rs, rowNum) -> {
        Enquiry e = new Enquiry();
        e.setId(rs.getLong("id"));
        long prodId = rs.getLong("product_id");
        if (!rs.wasNull()) {
            e.setProductId(prodId);
        }
        e.setCustomerId(rs.getLong("customer_id"));
        e.setArtisanId(rs.getLong("artisan_id"));
        e.setMessage(rs.getString("message"));
        e.setStatus(rs.getString("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            e.setCreatedAt(createdAt.toInstant().atOffset(ZoneOffset.UTC));
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            e.setUpdatedAt(updatedAt.toInstant().atOffset(ZoneOffset.UTC));
        }

        // Optional metadata from joins if present
        try {
            e.setProductName(rs.getString("product_name"));
        } catch (Exception ignored) {}
        try {
            e.setCustomerName(rs.getString("customer_name"));
        } catch (Exception ignored) {}
        try {
            e.setCustomerEmail(rs.getString("customer_email"));
        } catch (Exception ignored) {}
        try {
            e.setCustomerPhone(rs.getString("customer_phone"));
        } catch (Exception ignored) {}

        return e;
    };

    @Override
    public Enquiry save(Enquiry enquiry) {
        if (enquiry.getId() == null) {
            String sql = """
                INSERT INTO enquiries (product_id, customer_id, artisan_id, customer_name, customer_email, customer_phone, message, status, created_at, updated_at)
                VALUES (:productId, :customerId, :artisanId, :customerName, :customerEmail, :customerPhone, :message, :status, :createdAt, :updatedAt)
                """;
            KeyHolder keyHolder = new GeneratedKeyHolder();
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("productId", enquiry.getProductId())
                    .addValue("customerId", enquiry.getCustomerId())
                    .addValue("artisanId", enquiry.getArtisanId())
                    .addValue("customerName", enquiry.getCustomerName())
                    .addValue("customerEmail", enquiry.getCustomerEmail())
                    .addValue("customerPhone", enquiry.getCustomerPhone())
                    .addValue("message", enquiry.getMessage())
                    .addValue("status", enquiry.getStatus() != null ? enquiry.getStatus() : "PENDING")
                    .addValue("createdAt", OffsetDateTime.now())
                    .addValue("updatedAt", OffsetDateTime.now());

            jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
            Number id = keyHolder.getKey();
            if (id != null) {
                enquiry.setId(id.longValue());
            }
            return enquiry;
        } else {
            String sql = "UPDATE enquiries SET status = :status, message = :message, updated_at = :updatedAt WHERE id = :id";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", enquiry.getId())
                    .addValue("status", enquiry.getStatus())
                    .addValue("message", enquiry.getMessage())
                    .addValue("updatedAt", OffsetDateTime.now());
            jdbcTemplate.update(sql, params);
            return enquiry;
        }
    }

    @Override
    public Optional<Enquiry> findById(Long id) {
        String sql = """
            SELECT e.*, p.name as product_name,
                   COALESCE(e.customer_name, u.name) as customer_name,
                   COALESCE(e.customer_email, u.email) as customer_email,
                   COALESCE(e.customer_phone, u.phone) as customer_phone
            FROM enquiries e
            LEFT JOIN products p ON e.product_id = p.id
            JOIN users u ON e.customer_id = u.id
            WHERE e.id = :id
            """;
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, params, enquiryRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Enquiry> findAll(String status) {
        StringBuilder sql = new StringBuilder("""
            SELECT e.*, p.name as product_name,
                   COALESCE(e.customer_name, u.name) as customer_name,
                   COALESCE(e.customer_email, u.email) as customer_email,
                   COALESCE(e.customer_phone, u.phone) as customer_phone
            FROM enquiries e
            LEFT JOIN products p ON e.product_id = p.id
            JOIN users u ON e.customer_id = u.id
            """);
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (status != null && !status.isBlank()) {
            sql.append(" WHERE e.status = :status");
            params.addValue("status", status.trim().toUpperCase());
        }
        sql.append(" ORDER BY e.created_at DESC");
        return jdbcTemplate.query(sql.toString(), params, enquiryRowMapper);
    }

    @Override
    public List<Enquiry> findByArtisanId(Long artisanId, String status) {
        StringBuilder sql = new StringBuilder("""
            SELECT e.*, p.name as product_name,
                   COALESCE(e.customer_name, u.name) as customer_name,
                   COALESCE(e.customer_email, u.email) as customer_email,
                   COALESCE(e.customer_phone, u.phone) as customer_phone
            FROM enquiries e
            LEFT JOIN products p ON e.product_id = p.id
            JOIN users u ON e.customer_id = u.id
            WHERE e.artisan_id = :artisanId
            """);
        MapSqlParameterSource params = new MapSqlParameterSource("artisanId", artisanId);

        if (status != null && !status.isBlank()) {
            sql.append(" AND e.status = :status");
            params.addValue("status", status.trim().toUpperCase());
        }
        sql.append(" ORDER BY e.created_at DESC");

        return jdbcTemplate.query(sql.toString(), params, enquiryRowMapper);
    }

    @Override
    public List<Enquiry> findByCustomerId(Long customerId) {
        String sql = """
            SELECT e.*, p.name as product_name,
                   COALESCE(e.customer_name, u.name) as customer_name,
                   COALESCE(e.customer_email, u.email) as customer_email,
                   COALESCE(e.customer_phone, u.phone) as customer_phone
            FROM enquiries e
            LEFT JOIN products p ON e.product_id = p.id
            JOIN users u ON e.customer_id = u.id
            WHERE e.customer_id = :customerId
            ORDER BY e.created_at DESC
            """;
        MapSqlParameterSource params = new MapSqlParameterSource("customerId", customerId);
        return jdbcTemplate.query(sql, params, enquiryRowMapper);
    }

    @Override
    public void updateStatus(Long id, String status) {
        String sql = "UPDATE enquiries SET status = :status, updated_at = :updatedAt WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("status", status.trim().toUpperCase())
                .addValue("updatedAt", OffsetDateTime.now());
        jdbcTemplate.update(sql, params);
    }
}
