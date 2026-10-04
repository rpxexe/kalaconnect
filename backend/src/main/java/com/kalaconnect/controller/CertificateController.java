package com.kalaconnect.controller;

import com.kalaconnect.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CertificateController(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static class CertificateDto {
        private Long id;
        private Long artisanId;
        private String artisanName;
        private String shgName;
        private int score;
        private int totalQuestions;
        private int percentage;
        private String status; // PENDING_APPROVAL, APPROVED, REJECTED
        private String adminNotes;
        private String approvedBy;
        private String createdAt;
        private String approvedAt;

        public CertificateDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getArtisanId() { return artisanId; }
        public void setArtisanId(Long artisanId) { this.artisanId = artisanId; }
        public String getArtisanName() { return artisanName; }
        public void setArtisanName(String artisanName) { this.artisanName = artisanName; }
        public String getShgName() { return shgName; }
        public void setShgName(String shgName) { this.shgName = shgName; }
        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }
        public int getTotalQuestions() { return totalQuestions; }
        public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
        public int getPercentage() { return percentage; }
        public void setPercentage(int percentage) { this.percentage = percentage; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getAdminNotes() { return adminNotes; }
        public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
        public String getApprovedBy() { return approvedBy; }
        public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public String getApprovedAt() { return approvedAt; }
        public void setApprovedAt(String approvedAt) { this.approvedAt = approvedAt; }
    }

    public static class RejectRequest {
        private String notes;
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<CertificateDto>> submitQuizResult(@RequestBody CertificateDto dto) {
        if (dto.getArtisanName() == null || dto.getArtisanName().isBlank()) {
            dto.setArtisanName("Artisan");
        }
        if (dto.getTotalQuestions() <= 0) {
            dto.setTotalQuestions(5);
        }
        if (dto.getPercentage() <= 0 && dto.getTotalQuestions() > 0) {
            dto.setPercentage((int) Math.round((dto.getScore() * 100.0) / dto.getTotalQuestions()));
        }

        String status = "PENDING_APPROVAL";

        String insertSql = "INSERT INTO artisan_certificates (artisan_id, artisan_name, shg_name, score, total_questions, percentage, status) " +
                "VALUES (:artisanId, :artisanName, :shgName, :score, :totalQuestions, :percentage, :status) RETURNING id, created_at";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("artisanId", dto.getArtisanId())
                .addValue("artisanName", dto.getArtisanName())
                .addValue("shgName", dto.getShgName() != null ? dto.getShgName() : "Self-Help Group")
                .addValue("score", dto.getScore())
                .addValue("totalQuestions", dto.getTotalQuestions())
                .addValue("percentage", dto.getPercentage())
                .addValue("status", status);

        Map<String, Object> result = jdbcTemplate.queryForMap(insertSql, params);
        dto.setId(((Number) result.get("id")).longValue());
        dto.setStatus(status);
        dto.setCreatedAt(String.valueOf(result.get("created_at")));

        return new ResponseEntity<>(ApiResponse.success("Assessment submitted! Certificate is pending NGO Admin review.", dto), HttpStatus.CREATED);
    }

    @GetMapping("/my-status")
    public ResponseEntity<ApiResponse<CertificateDto>> getMyStatus(
            @RequestParam(required = false) Long artisanId) {

        String sql = "SELECT id, artisan_id, artisan_name, shg_name, score, total_questions, percentage, status, admin_notes, approved_by, created_at, approved_at " +
                "FROM artisan_certificates " +
                (artisanId != null ? "WHERE artisan_id = :artisanId " : "") +
                "ORDER BY id DESC LIMIT 1";

        MapSqlParameterSource params = new MapSqlParameterSource();
        if (artisanId != null) {
            params.addValue("artisanId", artisanId);
        }

        List<CertificateDto> results = jdbcTemplate.query(sql, params, (rs, rowNum) -> mapCertificate(rs));
        if (results.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("No certificate submission found", null));
        }

        return ResponseEntity.ok(ApiResponse.success("Certificate status retrieved", results.get(0)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CertificateDto>>> getAllCertificates(
            @RequestParam(required = false) String status) {

        String sql = "SELECT id, artisan_id, artisan_name, shg_name, score, total_questions, percentage, status, admin_notes, approved_by, created_at, approved_at " +
                "FROM artisan_certificates " +
                (status != null && !status.isBlank() ? "WHERE status = :status " : "") +
                "ORDER BY created_at DESC";

        MapSqlParameterSource params = new MapSqlParameterSource();
        if (status != null && !status.isBlank()) {
            params.addValue("status", status.trim().toUpperCase());
        }

        List<CertificateDto> results = jdbcTemplate.query(sql, params, (rs, rowNum) -> mapCertificate(rs));
        return ResponseEntity.ok(ApiResponse.success("Certificates retrieved successfully", results));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<CertificateDto>> approveCertificate(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminName = (auth != null && auth.getName() != null && !auth.getName().isBlank())
                ? auth.getName()
                : "NGO Administrator";

        String sql = "UPDATE artisan_certificates SET status = 'APPROVED', approved_by = :adminName, approved_at = CURRENT_TIMESTAMP WHERE id = :id";
        int updated = jdbcTemplate.update(sql, new MapSqlParameterSource().addValue("adminName", adminName).addValue("id", id));

        if (updated == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Certificate request not found with ID: " + id));
        }

        String selectSql = "SELECT id, artisan_id, artisan_name, shg_name, score, total_questions, percentage, status, admin_notes, approved_by, created_at, approved_at FROM artisan_certificates WHERE id = :id";
        CertificateDto dto = jdbcTemplate.queryForObject(selectSql, new MapSqlParameterSource().addValue("id", id), (rs, rowNum) -> mapCertificate(rs));

        return ResponseEntity.ok(ApiResponse.success("Artisan Certificate approved successfully", dto));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<CertificateDto>> rejectCertificate(
            @PathVariable Long id,
            @RequestBody(required = false) RejectRequest body) {

        String notes = (body != null && body.getNotes() != null && !body.getNotes().isBlank())
                ? body.getNotes().trim()
                : "Assessment score requires improvement. Please review learning modules and re-attempt.";

        String sql = "UPDATE artisan_certificates SET status = 'REJECTED', admin_notes = :notes WHERE id = :id";
        int updated = jdbcTemplate.update(sql, new MapSqlParameterSource().addValue("notes", notes).addValue("id", id));

        if (updated == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Certificate request not found with ID: " + id));
        }

        String selectSql = "SELECT id, artisan_id, artisan_name, shg_name, score, total_questions, percentage, status, admin_notes, approved_by, created_at, approved_at FROM artisan_certificates WHERE id = :id";
        CertificateDto dto = jdbcTemplate.queryForObject(selectSql, new MapSqlParameterSource().addValue("id", id), (rs, rowNum) -> mapCertificate(rs));

        return ResponseEntity.ok(ApiResponse.success("Artisan Certificate rejected with feedback", dto));
    }

    private CertificateDto mapCertificate(java.sql.ResultSet rs) throws java.sql.SQLException {
        CertificateDto c = new CertificateDto();
        c.setId(rs.getLong("id"));
        c.setArtisanId((Long) rs.getObject("artisan_id"));
        c.setArtisanName(rs.getString("artisan_name"));
        c.setShgName(rs.getString("shg_name"));
        c.setScore(rs.getInt("score"));
        c.setTotalQuestions(rs.getInt("total_questions"));
        c.setPercentage(rs.getInt("percentage"));
        c.setStatus(rs.getString("status"));
        c.setAdminNotes(rs.getString("admin_notes"));
        c.setApprovedBy(rs.getString("approved_by"));
        c.setCreatedAt(rs.getString("created_at"));
        c.setApprovedAt(rs.getString("approved_at"));
        return c;
    }
}
