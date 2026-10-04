package com.kalaconnect.service;

import com.kalaconnect.dto.PagedResult;
import com.kalaconnect.dto.ProductRequestDto;
import com.kalaconnect.dto.ProductResponseDto;
import com.kalaconnect.exception.ResourceNotFoundException;
import com.kalaconnect.exception.UnauthorizedException;
import com.kalaconnect.model.Product;
import com.kalaconnect.model.ProductImage;
import com.kalaconnect.model.User;
import com.kalaconnect.model.UserRole;
import com.kalaconnect.repository.ArtisanRepository;
import com.kalaconnect.repository.ProductRepository;
import com.kalaconnect.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ArtisanRepository artisanRepository;

    public ProductService(ProductRepository productRepository, UserRepository userRepository, ArtisanRepository artisanRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.artisanRepository = artisanRepository;
    }

    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthorizedException("Authentication required to access product services");
        }
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));
    }

    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto dto) {
        User user = getAuthenticatedUser();

        Product product = new Product();
        product.setArtisanId(user.getId());
        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        product.setMaterial(dto.getMaterial());
        product.setCraftType(dto.getCraftType());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setLocation(dto.getLocation());
        product.setStatus(dto.getStatus() != null ? dto.getStatus() : "PUBLISHED");
        product.setViewsCount(0);
        product.setAiDescription(dto.getAiDescription());
        product.setAiCaption(dto.getAiCaption());
        product.setAiHashtags(dto.getAiHashtags());

        if (dto.getPhotos() != null && !dto.getPhotos().isEmpty()) {
            List<ProductImage> images = new ArrayList<>();
            for (int i = 0; i < dto.getPhotos().size(); i++) {
                ProductImage img = new ProductImage();
                img.setImageUrl(dto.getPhotos().get(i));
                img.setPrimary(i == 0);
                img.setDisplayOrder(i);
                images.add(img);
            }
            product.setImages(images);
        }

        Product saved = productRepository.save(product);
        return toDto(saved, user.getName());
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getMyProducts() {
        User user = getAuthenticatedUser();
        List<Product> products = productRepository.findByArtisanId(user.getId());
        return products.stream()
                .map(p -> toDto(p, user.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Increment views count on read
        productRepository.incrementViews(id);
        product.setViewsCount(product.getViewsCount() + 1);

        String artisanName = userRepository.findById(product.getArtisanId())
                .map(User::getName)
                .orElse("Artisan");

        return toDto(product, artisanName);
    }

    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductRequestDto dto) {
        User user = getAuthenticatedUser();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Strict authorization: only the owning artisan (or NGO admin) can update
        if (!product.getArtisanId().equals(user.getId()) && user.getRole() != UserRole.NGO_ADMIN) {
            throw new UnauthorizedException("You are not authorized to update this product");
        }

        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        product.setMaterial(dto.getMaterial());
        product.setCraftType(dto.getCraftType());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setLocation(dto.getLocation());
        if (dto.getStatus() != null) {
            product.setStatus(dto.getStatus());
        }
        if (dto.getAiDescription() != null) {
            product.setAiDescription(dto.getAiDescription());
        }
        if (dto.getAiCaption() != null) {
            product.setAiCaption(dto.getAiCaption());
        }
        if (dto.getAiHashtags() != null) {
            product.setAiHashtags(dto.getAiHashtags());
        }

        if (dto.getPhotos() != null) {
            List<ProductImage> images = new ArrayList<>();
            for (int i = 0; i < dto.getPhotos().size(); i++) {
                ProductImage img = new ProductImage();
                img.setImageUrl(dto.getPhotos().get(i));
                img.setPrimary(i == 0);
                img.setDisplayOrder(i);
                images.add(img);
            }
            product.setImages(images);
        }

        Product saved = productRepository.save(product);
        return toDto(saved, user.getName());
    }

    @Transactional
    public void deleteProduct(Long id) {
        User user = getAuthenticatedUser();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (!product.getArtisanId().equals(user.getId()) && user.getRole() != UserRole.NGO_ADMIN) {
            throw new UnauthorizedException("You are not authorized to delete this product");
        }

        productRepository.deleteById(id);
    }

    @Transactional
    public ProductResponseDto updateProductStatus(Long id, String status) {
        User user = getAuthenticatedUser();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (!product.getArtisanId().equals(user.getId()) && user.getRole() != UserRole.NGO_ADMIN) {
            throw new UnauthorizedException("You are not authorized to update this product status");
        }

        productRepository.updateStatus(id, status);
        product.setStatus(status);
        return toDto(product, user.getName());
    }

    @Transactional(readOnly = true)
    public PagedResult<ProductResponseDto> getProducts(
            String searchQuery,
            String category,
            String craftType,
            String state,
            String district,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size) {

        int pageSize = (size <= 0) ? 20 : Math.min(size, 100);
        int pageNumber = Math.max(page, 0);
        int offset = pageNumber * pageSize;

        long total = productRepository.countProductsWithFilters(
                searchQuery, category, craftType, state, district, minPrice, maxPrice, null
        );

        List<Product> products = productRepository.searchProductsWithFilters(
                searchQuery, category, craftType, state, district, minPrice, maxPrice, null, pageSize, offset
        );

        List<ProductResponseDto> dtos = products.stream()
                .map(p -> {
                    String artisanName = userRepository.findById(p.getArtisanId())
                            .map(User::getName)
                            .orElse("Artisan");
                    return toDto(p, artisanName);
                })
                .collect(Collectors.toList());

        return PagedResult.of(dtos, pageNumber, pageSize, total);
    }

    private ProductResponseDto toDto(Product product, String artisanName) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setArtisanId(product.getArtisanId());
        dto.setArtisanName(artisanName);
        dto.setName(product.getName());
        dto.setCategory(product.getCategory());
        dto.setMaterial(product.getMaterial());
        dto.setCraftType(product.getCraftType());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setLocation(product.getLocation());
        dto.setStatus(product.getStatus());
        dto.setViews(product.getViewsCount());
        dto.setEnquiries(product.getEnquiriesCount());
        dto.setAiDescription(product.getAiDescription());
        dto.setAiCaption(product.getAiCaption());
        dto.setAiHashtags(product.getAiHashtags());

        if (product.getArtisanId() != null) {
            try {
                artisanRepository.findByUserId(product.getArtisanId()).ifPresent(ap -> {
                    dto.setArtisanProfileId(ap.getId());
                    if (ap.getShgName() != null && !ap.getShgName().isBlank()) {
                        dto.setShgName(ap.getShgName());
                    }
                    if (ap.getArtisanName() != null && !ap.getArtisanName().isBlank()) {
                        dto.setArtisanName(ap.getArtisanName());
                    }
                });
            } catch (Exception ignored) {}
        }

        String availability = (product.getQuantity() != null && product.getQuantity() > 0)
                ? "IN_STOCK"
                : "MADE_TO_ORDER";
        dto.setAvailability(availability);

        List<String> photoUrls = new ArrayList<>();
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            for (ProductImage img : product.getImages()) {
                String sanitized = sanitizeImageUrl(img.getImageUrl());
                if (sanitized != null && !sanitized.isBlank()) {
                    photoUrls.add(sanitized);
                }
            }
            if (!photoUrls.isEmpty()) {
                dto.setPrimaryPhoto(photoUrls.get(0));
            }
        }
        dto.setPhotos(photoUrls);

        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }

    private String sanitizeImageUrl(String url) {
        if (url == null || url.isBlank()) return null;
        String trimmed = url.trim();
        if (trimmed.contains("/api/images/")) {
            int idx = trimmed.indexOf("/api/images/");
            return trimmed.substring(idx); // Clean relative path: "/api/images/{id}"
        }
        return trimmed;
    }
}
