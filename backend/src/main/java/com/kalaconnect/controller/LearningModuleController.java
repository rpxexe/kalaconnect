package com.kalaconnect.controller;

import com.kalaconnect.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/learning/modules")
public class LearningModuleController {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public LearningModuleController(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static class LearningModuleDto {
        private String id;
        private String title;
        private String subtitle;
        private String category;
        private String duration;
        private String readTime;
        private String badgeText;
        private String summary;
        private List<String> keyPoints;
        private String detailedContent;
        private String practicalTip;
        private boolean isCompleted;

        public LearningModuleDto() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSubtitle() { return subtitle; }
        public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
        public String getReadTime() { return readTime; }
        public void setReadTime(String readTime) { this.readTime = readTime; }
        public String getBadgeText() { return badgeText; }
        public void setBadgeText(String badgeText) { this.badgeText = badgeText; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public List<String> getKeyPoints() { return keyPoints; }
        public void setKeyPoints(List<String> keyPoints) { this.keyPoints = keyPoints; }
        public String getDetailedContent() { return detailedContent; }
        public void setDetailedContent(String detailedContent) { this.detailedContent = detailedContent; }
        public String getPracticalTip() { return practicalTip; }
        public void setPracticalTip(String practicalTip) { this.practicalTip = practicalTip; }
        public boolean isCompleted() { return isCompleted; }
        public void setCompleted(boolean completed) { isCompleted = completed; }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LearningModuleDto>>> getModules() {
        seedDefaultsIfEmpty();

        String sql = "SELECT id, title, subtitle, category, duration, read_time, badge_text, summary, key_points, detailed_content, practical_tip FROM learning_modules ORDER BY created_at ASC";
        List<LearningModuleDto> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            LearningModuleDto dto = new LearningModuleDto();
            dto.setId(rs.getString("id"));
            dto.setTitle(rs.getString("title"));
            dto.setSubtitle(rs.getString("subtitle"));
            dto.setCategory(rs.getString("category"));
            dto.setDuration(rs.getString("duration"));
            dto.setReadTime(rs.getString("read_time"));
            dto.setBadgeText(rs.getString("badge_text"));
            dto.setSummary(rs.getString("summary"));
            dto.setDetailedContent(rs.getString("detailed_content"));
            dto.setPracticalTip(rs.getString("practical_tip"));

            String rawPoints = rs.getString("key_points");
            if (rawPoints != null && !rawPoints.isBlank()) {
                dto.setKeyPoints(Arrays.asList(rawPoints.split("\\r?\\n|;;")));
            } else {
                dto.setKeyPoints(new ArrayList<>());
            }
            return dto;
        });

        return ResponseEntity.ok(ApiResponse.success("Learning modules retrieved successfully", list));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LearningModuleDto>> createModule(@RequestBody LearningModuleDto dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Module title is required"));
        }

        if (dto.getId() == null || dto.getId().isBlank()) {
            dto.setId("mod_" + UUID.randomUUID().toString().substring(0, 8));
        }
        if (dto.getCategory() == null || dto.getCategory().isBlank()) {
            dto.setCategory("Artisan Business");
        }
        if (dto.getDuration() == null || dto.getDuration().isBlank()) {
            dto.setDuration("15 mins");
        }
        if (dto.getReadTime() == null || dto.getReadTime().isBlank()) {
            dto.setReadTime("5 min read");
        }
        if (dto.getBadgeText() == null || dto.getBadgeText().isBlank()) {
            dto.setBadgeText("Foundational");
        }

        String joinedPoints = "";
        if (dto.getKeyPoints() != null && !dto.getKeyPoints().isEmpty()) {
            joinedPoints = String.join("\n", dto.getKeyPoints());
        }

        String sql = "INSERT INTO learning_modules (id, title, subtitle, category, duration, read_time, badge_text, summary, key_points, detailed_content, practical_tip) " +
                "VALUES (:id, :title, :subtitle, :category, :duration, :readTime, :badgeText, :summary, :keyPoints, :detailedContent, :practicalTip) " +
                "ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title, subtitle = EXCLUDED.subtitle, category = EXCLUDED.category, duration = EXCLUDED.duration, " +
                "read_time = EXCLUDED.read_time, badge_text = EXCLUDED.badge_text, summary = EXCLUDED.summary, key_points = EXCLUDED.key_points, detailed_content = EXCLUDED.detailed_content, practical_tip = EXCLUDED.practical_tip";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", dto.getId())
                .addValue("title", dto.getTitle())
                .addValue("subtitle", dto.getSubtitle() != null ? dto.getSubtitle() : dto.getSummary())
                .addValue("category", dto.getCategory())
                .addValue("duration", dto.getDuration())
                .addValue("readTime", dto.getReadTime())
                .addValue("badgeText", dto.getBadgeText())
                .addValue("summary", dto.getSummary())
                .addValue("keyPoints", joinedPoints)
                .addValue("detailedContent", dto.getDetailedContent())
                .addValue("practicalTip", dto.getPracticalTip());

        jdbcTemplate.update(sql, params);
        return new ResponseEntity<>(ApiResponse.success("Learning topic created successfully", dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteModule(@PathVariable String id) {
        String sql = "DELETE FROM learning_modules WHERE id = :id";
        jdbcTemplate.update(sql, new MapSqlParameterSource().addValue("id", id));
        return ResponseEntity.ok(ApiResponse.success("Learning topic deleted successfully", null));
    }

    private synchronized void seedDefaultsIfEmpty() {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM learning_modules", new MapSqlParameterSource(), Integer.class);
            if (count != null && count > 0) {
                return;
            }

            insertModule(
                    "mod_digital_showcase",
                    "Smartphone Photography & Digital Storytelling",
                    "Present your genuine handicraft in soft daylight for urban buyers",
                    "Digital Literacy",
                    "15 mins",
                    "4 min read",
                    "Foundational",
                    "Learn to photograph your handcrafted works using any smartphone and craft authentic narratives that connect emotionally with customers.",
                    "1. Always use soft indirect morning sunlight near a window.\n2. Keep backgrounds clean and earthy (linen, plain wood, neutral walls).\n3. Take close-ups showing intricate weave or brush texture.\n4. Mention your cluster, village, and ancestral heritage.",
                    "High-resolution mobile photos make handcrafted art stand out online. Avoid harsh flashes which wash out natural dyes and textures. When composing your shot, place the craft piece slightly off-center using the Rule of Thirds. Share the artisan story behind every motif: who taught you this art, what the pattern represents, and how many days it took to hand-finish.",
                    "Place a small coin or clay diya beside delicate jewelry or pottery to give buyers an intuitive sense of physical scale."
            );

            insertModule(
                    "mod_pricing_strategy",
                    "Transparent Pricing & Artisan Costing",
                    "Calculate fair remuneration ensuring healthy sustainable livelihoods",
                    "Financial Literacy",
                    "20 mins",
                    "5 min read",
                    "Business Pro",
                    "Never underprice your work. Master calculating raw material costs, hourly artisan wages, packaging, and community group margins.",
                    "1. Material Cost + Labor Hours × Living Wage + Overheads = Base Cost.\n2. Add 15-20% SHG collective development margin.\n3. Keep written records of raw thread, brass, clay, or dye purchases.\n4. State transparent artisan pricing to build buyer trust.",
                    "Artisans often overlook their own time. Treat your skilled labor hours as valuable expertise. Calculate total hours spent preparing raw materials, shaping, weaving, or painting. Add standard transport and protective packaging expenses so every sale provides sustainable family revenue.",
                    "Keep a simple pocket ledger where you write down the purchase date and price of all raw supplies."
            );

            insertModule(
                    "mod_secure_payments",
                    "Direct UPI & Safe Banking for SHGs",
                    "Receive direct customer payments straight into your group account",
                    "Banking & UPI",
                    "10 mins",
                    "3 min read",
                    "Essential",
                    "Protect your earnings by setting up direct bank UPI QR codes, avoiding middlemen and ensuring timely receipt notifications.",
                    "1. Set up an official bank UPI QR code tied to your artisan account.\n2. Never share your 4 or 6 digit UPI PIN with any caller.\n3. Always verify incoming payment SMS or voice notifications before dispatching goods.\n4. Keep transaction records for SHG bookkeeping.",
                    "Direct digital banking eliminates middlemen commissions and delayed payouts. With UPI, customers in major metros can scan your verified QR code and pay directly into your account in seconds. Remember: Receiving money NEVER requires entering your PIN.",
                    "Stick your laminated UPI QR code right near your workshop bench or on your exhibition table for instant scanning."
            );

            insertModule(
                    "mod_packaging_dispatch",
                    "Safe Packaging & Eco-Friendly Courier Dispatch",
                    "Protect fragile ceramics, paintings, and sarees during transit",
                    "Packaging & Shipping",
                    "15 mins",
                    "4 min read",
                    "Craft Quality",
                    "Zero-breakage packaging techniques using biodegradable honeycomb wrap, corrugated boxes, and moisture-proof layering.",
                    "1. Wrap fragile terracotta or pottery in multi-layer honeycomb kraft paper.\n2. Place dry silica or neem leaves with handloom textiles to prevent dampness.\n3. Seal sturdy outer boxes with reinforced water-activated tape.\n4. Include a handwritten thank-you note from your artisan cluster.",
                    "Transit damages hurt customer trust and artisan revenues. Learn standard parcel cushioning methods. For textiles and handloom sarees, use breathable cotton bags inside outer waterproof envelopes. For clay, metalwork, and framed folk art, ensure at least 2 inches of cushioning on all sides inside corrugated boxes.",
                    "Include a small signed card with your cluster's name and signature: customers love knowing exactly who crafted their treasure."
            );
        } catch (Exception ignored) {}
    }

    private void insertModule(String id, String title, String subtitle, String category,
                              String duration, String readTime, String badgeText,
                              String summary, String keyPoints, String detailedContent, String practicalTip) {
        String sql = "INSERT INTO learning_modules (id, title, subtitle, category, duration, read_time, badge_text, summary, key_points, detailed_content, practical_tip) " +
                "VALUES (:id, :title, :subtitle, :category, :duration, :readTime, :badgeText, :summary, :keyPoints, :detailedContent, :practicalTip) " +
                "ON CONFLICT (id) DO NOTHING";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("title", title)
                .addValue("subtitle", subtitle)
                .addValue("category", category)
                .addValue("duration", duration)
                .addValue("readTime", readTime)
                .addValue("badgeText", badgeText)
                .addValue("summary", summary)
                .addValue("keyPoints", keyPoints)
                .addValue("detailedContent", detailedContent)
                .addValue("practicalTip", practicalTip);

        jdbcTemplate.update(sql, params);
    }
}
