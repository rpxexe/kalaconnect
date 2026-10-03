package com.kalaconnect.repository;

import com.kalaconnect.model.Product;
import com.kalaconnect.model.ProductImage;
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
public class ProductRepositoryImpl implements ProductRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ProductRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Product> productRowMapper = (rs, rowNum) -> {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setArtisanId(rs.getLong("artisan_id"));
        p.setName(rs.getString("name"));
        p.setCategory(rs.getString("category"));
        p.setMaterial(rs.getString("material"));
        p.setCraftType(rs.getString("craft_type"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setQuantity(rs.getInt("quantity"));
        p.setLocation(rs.getString("location"));
        p.setStatus(rs.getString("status"));
        try {
            p.setViewsCount(rs.getInt("views_count"));
        } catch (Exception ignored) {}
        p.setAiDescription(rs.getString("ai_description"));
        p.setAiCaption(rs.getString("ai_caption"));
        p.setAiHashtags(rs.getString("ai_hashtags"));

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

    private final RowMapper<ProductImage> imageRowMapper = (rs, rowNum) -> {
        ProductImage img = new ProductImage();
        img.setId(rs.getLong("id"));
        img.setProductId(rs.getLong("product_id"));
        img.setImageUrl(rs.getString("image_url"));
        img.setPrimary(rs.getBoolean("is_primary"));
        img.setDisplayOrder(rs.getInt("display_order"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            img.setCreatedAt(createdAt.toInstant().atOffset(ZoneOffset.UTC));
        }
        return img;
    };

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            String sql = """
                INSERT INTO products (artisan_id, name, category, material, craft_type, description, price, quantity, location, status, views_count, ai_description, ai_caption, ai_hashtags, created_at, updated_at)
                VALUES (:artisanId, :name, :category, :material, :craftType, :description, :price, :quantity, :location, :status, :viewsCount, :aiDescription, :aiCaption, :aiHashtags, :createdAt, :updatedAt)
                """;

            KeyHolder keyHolder = new GeneratedKeyHolder();
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("artisanId", product.getArtisanId())
                    .addValue("name", product.getName())
                    .addValue("category", product.getCategory())
                    .addValue("material", product.getMaterial())
                    .addValue("craftType", product.getCraftType())
                    .addValue("description", product.getDescription())
                    .addValue("price", product.getPrice())
                    .addValue("quantity", product.getQuantity())
                    .addValue("location", product.getLocation())
                    .addValue("status", product.getStatus() != null ? product.getStatus() : "AVAILABLE")
                    .addValue("viewsCount", product.getViewsCount())
                    .addValue("aiDescription", product.getAiDescription())
                    .addValue("aiCaption", product.getAiCaption())
                    .addValue("aiHashtags", product.getAiHashtags())
                    .addValue("createdAt", OffsetDateTime.now())
                    .addValue("updatedAt", OffsetDateTime.now());

            jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
            Number id = keyHolder.getKey();
            if (id != null) {
                product.setId(id.longValue());
            }
        } else {
            String sql = """
                UPDATE products
                SET name = :name, category = :category, material = :material, craft_type = :craftType,
                    description = :description, price = :price, quantity = :quantity, location = :location,
                    status = :status, ai_description = :aiDescription, ai_caption = :aiCaption,
                    ai_hashtags = :aiHashtags, updated_at = :updatedAt
                WHERE id = :id
                """;
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", product.getId())
                    .addValue("name", product.getName())
                    .addValue("category", product.getCategory())
                    .addValue("material", product.getMaterial())
                    .addValue("craftType", product.getCraftType())
                    .addValue("description", product.getDescription())
                    .addValue("price", product.getPrice())
                    .addValue("quantity", product.getQuantity())
                    .addValue("location", product.getLocation())
                    .addValue("status", product.getStatus())
                    .addValue("aiDescription", product.getAiDescription())
                    .addValue("aiCaption", product.getAiCaption())
                    .addValue("aiHashtags", product.getAiHashtags())
                    .addValue("updatedAt", OffsetDateTime.now());

            jdbcTemplate.update(sql, params);
        }

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            deleteProductImages(product.getId());
            for (int i = 0; i < product.getImages().size(); i++) {
                ProductImage img = product.getImages().get(i);
                img.setProductId(product.getId());
                img.setDisplayOrder(i);
                img.setPrimary(i == 0);
                addProductImage(img);
            }
        }

        enrichProduct(product);
        return product;
    }

    @Override
    public Optional<Product> findById(Long id) {
        String sql = "SELECT * FROM products WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            Product product = jdbcTemplate.queryForObject(sql, params, productRowMapper);
            if (product != null) {
                enrichProduct(product);
            }
            return Optional.ofNullable(product);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Product> findByArtisanId(Long artisanId) {
        String sql = "SELECT * FROM products WHERE artisan_id = :artisanId ORDER BY created_at DESC";
        MapSqlParameterSource params = new MapSqlParameterSource("artisanId", artisanId);
        List<Product> products = jdbcTemplate.query(sql, params, productRowMapper);
        for (Product product : products) {
            enrichProduct(product);
        }
        return products;
    }

    @Override
    public List<Product> searchProducts(String category, String searchQuery, String status, int limit, int offset) {
        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1 ");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (category != null && !category.isBlank()) {
            sql.append("AND LOWER(category) = LOWER(:category) ");
            params.addValue("category", category.trim());
        }
        if (status != null && !status.isBlank()) {
            sql.append("AND status = :status ");
            params.addValue("status", status.trim());
        } else {
            sql.append("AND status = 'AVAILABLE' ");
        }
        if (searchQuery != null && !searchQuery.isBlank()) {
            sql.append("AND (LOWER(name) LIKE :searchLike OR LOWER(description) LIKE :searchLike OR LOWER(craft_type) LIKE :searchLike) ");
            params.addValue("searchLike", "%" + searchQuery.trim().toLowerCase() + "%");
        }

        sql.append("ORDER BY created_at DESC LIMIT :limit OFFSET :offset");
        params.addValue("limit", limit > 0 ? limit : 20);
        params.addValue("offset", Math.max(offset, 0));

        List<Product> products = jdbcTemplate.query(sql.toString(), params, productRowMapper);
        for (Product product : products) {
            product.setImages(getProductImages(product.getId()));
        }
        return products;
    }

    @Override
    public List<Product> searchProductsWithFilters(
            String searchQuery,
            String category,
            String craftType,
            String state,
            String district,
            java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice,
            String status,
            int limit,
            int offset) {

        StringBuilder sql = new StringBuilder("""
            SELECT DISTINCT p.* FROM products p
            LEFT JOIN artisan_profiles ap ON ap.user_id = p.artisan_id
            LEFT JOIN users u ON u.id = p.artisan_id
            LEFT JOIN artisan_skills ask ON ask.artisan_id = ap.id
            LEFT JOIN skills s ON s.id = ask.skill_id
            WHERE 1=1
            """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        buildFilterClauses(sql, params, searchQuery, category, craftType, state, district, minPrice, maxPrice, status);

        sql.append(" ORDER BY p.created_at DESC LIMIT :limit OFFSET :offset");
        params.addValue("limit", limit > 0 ? limit : 20);
        params.addValue("offset", Math.max(offset, 0));

        List<Product> products = jdbcTemplate.query(sql.toString(), params, productRowMapper);
        for (Product product : products) {
            product.setImages(getProductImages(product.getId()));
        }
        return products;
    }

    @Override
    public long countProductsWithFilters(
            String searchQuery,
            String category,
            String craftType,
            String state,
            String district,
            java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice,
            String status) {

        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(DISTINCT p.id) FROM products p
            LEFT JOIN artisan_profiles ap ON ap.user_id = p.artisan_id
            LEFT JOIN users u ON u.id = p.artisan_id
            LEFT JOIN artisan_skills ask ON ask.artisan_id = ap.id
            LEFT JOIN skills s ON s.id = ask.skill_id
            WHERE 1=1
            """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        buildFilterClauses(sql, params, searchQuery, category, craftType, state, district, minPrice, maxPrice, status);

        Long count = jdbcTemplate.queryForObject(sql.toString(), params, Long.class);
        return count != null ? count : 0L;
    }

    private void buildFilterClauses(
            StringBuilder sql,
            MapSqlParameterSource params,
            String searchQuery,
            String category,
            String craftType,
            String state,
            String district,
            java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice,
            String status) {

        if (status != null && !status.isBlank() && !status.equalsIgnoreCase("ALL")) {
            sql.append(" AND LOWER(p.status) = LOWER(:status)");
            params.addValue("status", status.trim());
        } else {
            sql.append(" AND (p.status = 'AVAILABLE' OR p.status = 'PUBLISHED')");
        }

        if (category != null && !category.isBlank() && !category.equalsIgnoreCase("ALL")) {
            sql.append(" AND LOWER(p.category) = LOWER(:category)");
            params.addValue("category", category.trim());
        }

        if (craftType != null && !craftType.isBlank() && !craftType.equalsIgnoreCase("ALL")) {
            sql.append(" AND LOWER(p.craft_type) = LOWER(:craftType)");
            params.addValue("craftType", craftType.trim());
        }

        if (state != null && !state.isBlank() && !state.equalsIgnoreCase("ALL")) {
            sql.append(" AND (LOWER(ap.state) = LOWER(:state) OR LOWER(p.location) LIKE :stateLike)");
            params.addValue("state", state.trim());
            params.addValue("stateLike", "%" + state.trim().toLowerCase() + "%");
        }

        if (district != null && !district.isBlank() && !district.equalsIgnoreCase("ALL")) {
            sql.append(" AND (LOWER(ap.district) = LOWER(:district) OR LOWER(p.location) LIKE :districtLike)");
            params.addValue("district", district.trim());
            params.addValue("districtLike", "%" + district.trim().toLowerCase() + "%");
        }

        if (minPrice != null) {
            sql.append(" AND p.price >= :minPrice");
            params.addValue("minPrice", minPrice);
        }

        if (maxPrice != null) {
            sql.append(" AND p.price <= :maxPrice");
            params.addValue("maxPrice", maxPrice);
        }

        if (searchQuery != null && !searchQuery.isBlank()) {
            String q = "%" + searchQuery.trim().toLowerCase() + "%";
            sql.append(" AND (LOWER(p.name) LIKE :q OR LOWER(p.description) LIKE :q OR LOWER(p.category) LIKE :q " +
                    "OR LOWER(p.craft_type) LIKE :q OR LOWER(p.material) LIKE :q OR LOWER(ap.artisan_name) LIKE :q " +
                    "OR LOWER(u.name) LIKE :q OR LOWER(ap.shg_name) LIKE :q OR LOWER(s.name) LIKE :q)");
            params.addValue("q", q);
        }
    }

    @Override
    public void addProductImage(ProductImage image) {
        String sql = """
            INSERT INTO product_images (product_id, image_url, is_primary, display_order, created_at)
            VALUES (:productId, :imageUrl, :isPrimary, :displayOrder, :createdAt)
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("productId", image.getProductId())
                .addValue("imageUrl", image.getImageUrl())
                .addValue("isPrimary", image.isPrimary())
                .addValue("displayOrder", image.getDisplayOrder() != null ? image.getDisplayOrder() : 0)
                .addValue("createdAt", OffsetDateTime.now());

        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<ProductImage> getProductImages(Long productId) {
        String sql = "SELECT * FROM product_images WHERE product_id = :productId ORDER BY is_primary DESC, display_order ASC, created_at ASC";
        MapSqlParameterSource params = new MapSqlParameterSource("productId", productId);
        return jdbcTemplate.query(sql, params, imageRowMapper);
    }

    @Override
    public void deleteProductImages(Long productId) {
        String sql = "DELETE FROM product_images WHERE product_id = :productId";
        MapSqlParameterSource params = new MapSqlParameterSource("productId", productId);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM products WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void updateStatus(Long id, String status) {
        String sql = "UPDATE products SET status = :status, updated_at = :updatedAt WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("status", status)
                .addValue("updatedAt", OffsetDateTime.now());
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void incrementViews(Long id) {
        String sql = "UPDATE products SET views_count = COALESCE(views_count, 0) + 1, updated_at = :updatedAt WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("updatedAt", OffsetDateTime.now());
        jdbcTemplate.update(sql, params);
    }

    private void enrichProduct(Product p) {
        if (p == null) return;
        p.setImages(getProductImages(p.getId()));
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM enquiries WHERE product_id = :productId",
                    new MapSqlParameterSource("productId", p.getId()),
                    Integer.class
            );
            p.setEnquiriesCount(count != null ? count : 0);
        } catch (Exception e) {
            p.setEnquiriesCount(0);
        }
    }
}
