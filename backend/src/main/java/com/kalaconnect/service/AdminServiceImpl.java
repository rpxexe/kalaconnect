package com.kalaconnect.service;

import com.kalaconnect.dto.AdminArtisanUpdateDto;
import com.kalaconnect.dto.AdminDashboardMetricsDto;
import com.kalaconnect.dto.ArtisanProfileDto;
import com.kalaconnect.dto.PagedResult;
import com.kalaconnect.dto.ProductResponseDto;
import com.kalaconnect.exception.ResourceNotFoundException;
import com.kalaconnect.exception.UnauthorizedException;
import com.kalaconnect.model.AdminAction;
import com.kalaconnect.model.ArtisanProfile;
import com.kalaconnect.model.Enquiry;
import com.kalaconnect.model.Product;
import com.kalaconnect.model.ProductImage;
import com.kalaconnect.model.User;
import com.kalaconnect.model.UserRole;
import com.kalaconnect.repository.AdminRepository;
import com.kalaconnect.repository.ArtisanRepository;
import com.kalaconnect.repository.EnquiryRepository;
import com.kalaconnect.repository.ProductRepository;
import com.kalaconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final ArtisanRepository artisanRepository;
    private final ProductRepository productRepository;
    private final EnquiryRepository enquiryRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public AdminServiceImpl(AdminRepository adminRepository,
                            ArtisanRepository artisanRepository,
                            ProductRepository productRepository,
                            EnquiryRepository enquiryRepository,
                            UserRepository userRepository,
                            NotificationService notificationService) {
        this.adminRepository = adminRepository;
        this.artisanRepository = artisanRepository;
        this.productRepository = productRepository;
        this.enquiryRepository = enquiryRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    private User getAdminUser(String adminEmail) {
        if (adminEmail == null || adminEmail.isBlank()) {
            throw new UnauthorizedException("Admin authentication required");
        }
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new UnauthorizedException("Admin account not found"));
        if (admin.getRole() != UserRole.NGO_ADMIN) {
            throw new UnauthorizedException("User does not have NGO_ADMIN privileges");
        }
        return admin;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardMetricsDto getDashboardMetrics() {
        return adminRepository.getDashboardMetrics();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<ArtisanProfileDto> getArtisans(
            String searchQuery,
            String craftCategory,
            String skill,
            String state,
            String district,
            String approvalStatus,
            int page,
            int size) {

        int pageSize = (size <= 0) ? 20 : Math.min(size, 100);
        int pageNumber = Math.max(page, 0);
        int offset = pageNumber * pageSize;

        long total = artisanRepository.countArtisansWithFilters(
                searchQuery, craftCategory, skill, state, district, approvalStatus
        );

        List<ArtisanProfile> profiles = artisanRepository.searchArtisansWithFilters(
                searchQuery, craftCategory, skill, state, district, approvalStatus, pageSize, offset
        );

        List<ArtisanProfileDto> dtos = profiles.stream()
                .map(p -> {
                    User u = (p.getUserId() != null) ? userRepository.findById(p.getUserId()).orElse(null) : null;
                    return toArtisanDto(p, u);
                })
                .collect(Collectors.toList());

        return PagedResult.of(dtos, pageNumber, pageSize, total);
    }

    @Override
    @Transactional
    public ArtisanProfileDto approveArtisan(Long artisanId, String adminEmail, String reason) {
        User admin = getAdminUser(adminEmail);

        ArtisanProfile profile = findArtisanProfileOrThrow(artisanId);
        artisanRepository.updateApprovalStatus(profile.getId(), "APPROVED");
        profile.setApprovalStatus("APPROVED");

        if (profile.getUserId() != null) {
            userRepository.findById(profile.getUserId()).ifPresent(user -> {
                user.setVerified(true);
                userRepository.save(user);
            });

            // Trigger notification to the artisan
            notificationService.createNotification(
                    profile.getUserId(),
                    "Profile Approved",
                    "Your artisan profile and SHG credentials have been verified and approved by the NGO Admin. Your products are now live on the marketplace.",
                    "VERIFICATION"
            );
        }

        // Record admin audit log
        AdminAction action = new AdminAction();
        action.setAdminId(admin.getId());
        action.setActionType("APPROVE_ARTISAN");
        action.setTargetType("ARTISAN");
        action.setTargetId(profile.getId());
        action.setReason(reason != null && !reason.isBlank() ? reason.trim() : "Approved by NGO Admin");
        action.setCreatedAt(OffsetDateTime.now());
        adminRepository.recordAdminAction(action);

        User user = (profile.getUserId() != null) ? userRepository.findById(profile.getUserId()).orElse(null) : null;
        return toArtisanDto(profile, user);
    }

    @Override
    @Transactional
    public ArtisanProfileDto rejectArtisan(Long artisanId, String adminEmail, String reason) {
        User admin = getAdminUser(adminEmail);

        ArtisanProfile profile = findArtisanProfileOrThrow(artisanId);
        artisanRepository.updateApprovalStatus(profile.getId(), "REJECTED");
        profile.setApprovalStatus("REJECTED");

        if (profile.getUserId() != null) {
            userRepository.findById(profile.getUserId()).ifPresent(user -> {
                user.setVerified(false);
                userRepository.save(user);
            });

            String note = (reason != null && !reason.isBlank())
                    ? "Feedback: " + reason.trim()
                    : "Please review your craft information and contact details, then resubmit.";

            notificationService.createNotification(
                    profile.getUserId(),
                    "Profile Verification Update",
                    "Your artisan registration was not approved. " + note,
                    "VERIFICATION"
            );
        }

        AdminAction action = new AdminAction();
        action.setAdminId(admin.getId());
        action.setActionType("REJECT_ARTISAN");
        action.setTargetType("ARTISAN");
        action.setTargetId(profile.getId());
        action.setReason(reason != null && !reason.isBlank() ? reason.trim() : "Rejected by NGO Admin");
        action.setCreatedAt(OffsetDateTime.now());
        adminRepository.recordAdminAction(action);

        User user = (profile.getUserId() != null) ? userRepository.findById(profile.getUserId()).orElse(null) : null;
        return toArtisanDto(profile, user);
    }

    @Override
    @Transactional
    public ArtisanProfileDto updateArtisan(Long artisanId, AdminArtisanUpdateDto updateDto, String adminEmail) {
        User admin = getAdminUser(adminEmail);

        ArtisanProfile profile = findArtisanProfileOrThrow(artisanId);

        if (updateDto.getArtisanName() != null) profile.setArtisanName(updateDto.getArtisanName());
        if (updateDto.getShgName() != null) profile.setShgName(updateDto.getShgName());
        if (updateDto.getBio() != null) profile.setBio(updateDto.getBio());
        if (updateDto.getLocation() != null) profile.setLocation(updateDto.getLocation());
        if (updateDto.getDistrict() != null) profile.setDistrict(updateDto.getDistrict());
        if (updateDto.getState() != null) profile.setState(updateDto.getState());
        if (updateDto.getExperience() != null) profile.setExperience(updateDto.getExperience());
        if (updateDto.getContactPreference() != null) profile.setContactPreference(updateDto.getContactPreference());

        if (updateDto.getApprovalStatus() != null && !updateDto.getApprovalStatus().isBlank()) {
            String status = updateDto.getApprovalStatus().trim().toUpperCase();
            profile.setApprovalStatus(status);
            artisanRepository.updateApprovalStatus(profile.getId(), status);
        }

        artisanRepository.save(profile);

        if (updateDto.getSkills() != null && profile.getUserId() != null) {
            artisanRepository.syncArtisanSkills(profile.getId(), profile.getUserId(), updateDto.getSkills());
        }

        AdminAction action = new AdminAction();
        action.setAdminId(admin.getId());
        action.setActionType("UPDATE_ARTISAN");
        action.setTargetType("ARTISAN");
        action.setTargetId(profile.getId());
        action.setReason("Artisan details updated by NGO Admin");
        action.setCreatedAt(OffsetDateTime.now());
        adminRepository.recordAdminAction(action);

        User user = (profile.getUserId() != null) ? userRepository.findById(profile.getUserId()).orElse(null) : null;
        return toArtisanDto(profile, user);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<ProductResponseDto> getProducts(
            String searchQuery,
            String category,
            String craftType,
            String state,
            String district,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String status,
            int page,
            int size) {

        int pageSize = (size <= 0) ? 20 : Math.min(size, 100);
        int pageNumber = Math.max(page, 0);
        int offset = pageNumber * pageSize;

        long total = productRepository.countProductsWithFilters(
                searchQuery, category, craftType, state, district, minPrice, maxPrice, status
        );

        List<Product> products = productRepository.searchProductsWithFilters(
                searchQuery, category, craftType, state, district, minPrice, maxPrice, status, pageSize, offset
        );

        List<ProductResponseDto> dtos = products.stream()
                .map(this::toProductDto)
                .collect(Collectors.toList());

        return PagedResult.of(dtos, pageNumber, pageSize, total);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Enquiry> getEnquiries(String status) {
        String queryStatus = (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status))
                ? status.trim().toUpperCase()
                : null;
        return enquiryRepository.findAll(queryStatus);
    }

    private ArtisanProfile findArtisanProfileOrThrow(Long artisanId) {
        return artisanRepository.findById(artisanId)
                .or(() -> artisanRepository.findByUserId(artisanId))
                .orElseThrow(() -> new ResourceNotFoundException("Artisan profile not found with ID: " + artisanId));
    }

    private ArtisanProfileDto toArtisanDto(ArtisanProfile profile, User user) {
        ArtisanProfileDto dto = new ArtisanProfileDto();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUserId());
        String defaultName = (user != null) ? user.getName() : "Artisan";
        dto.setArtisanName(profile.getArtisanName() != null ? profile.getArtisanName() : defaultName);
        dto.setShgName(profile.getShgName());
        dto.setBio(profile.getBio());
        dto.setVillageCity(profile.getLocation());
        dto.setDistrict(profile.getDistrict());
        dto.setState(profile.getState());
        dto.setExperience(profile.getExperience());
        dto.setContactPreference(profile.getContactPreference());
        String defaultImage = (user != null) ? user.getProfileImage() : null;
        dto.setProfilePhoto(profile.getProfileImage() != null ? profile.getProfileImage() : defaultImage);
        dto.setApprovalStatus(profile.getApprovalStatus());
        dto.setVerified(user != null && user.isVerified());
        dto.setSkills(profile.getSkills());
        dto.setCompletionPercentage(profile.getCompletionPercentage());
        dto.setCreatedAt(profile.getCreatedAt());
        dto.setUpdatedAt(profile.getUpdatedAt());
        return dto;
    }

    private ProductResponseDto toProductDto(Product product) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setArtisanId(product.getArtisanId());
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
            userRepository.findById(product.getArtisanId()).ifPresent(u -> dto.setArtisanName(u.getName()));
            artisanRepository.findByUserId(product.getArtisanId()).ifPresent(ap -> {
                dto.setArtisanProfileId(ap.getId());
                if (ap.getShgName() != null && !ap.getShgName().isBlank()) {
                    dto.setShgName(ap.getShgName());
                }
                if (ap.getArtisanName() != null && !ap.getArtisanName().isBlank()) {
                    dto.setArtisanName(ap.getArtisanName());
                }
            });
        }

        String availability = (product.getQuantity() != null && product.getQuantity() > 0)
                ? "IN_STOCK"
                : "MADE_TO_ORDER";
        dto.setAvailability(availability);

        List<String> photoUrls = new ArrayList<>();
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            for (ProductImage img : product.getImages()) {
                photoUrls.add(img.getImageUrl());
            }
            dto.setPrimaryPhoto(product.getImages().get(0).getImageUrl());
        }
        dto.setPhotos(photoUrls);

        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }
}
