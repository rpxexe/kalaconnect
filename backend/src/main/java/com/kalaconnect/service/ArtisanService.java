package com.kalaconnect.service;

import com.kalaconnect.dto.ArtisanProfileDto;
import com.kalaconnect.dto.PagedResult;
import com.kalaconnect.dto.ProductResponseDto;
import com.kalaconnect.exception.ResourceNotFoundException;
import com.kalaconnect.exception.UnauthorizedException;
import com.kalaconnect.model.ArtisanProfile;
import com.kalaconnect.model.Product;
import com.kalaconnect.model.ProductImage;
import com.kalaconnect.model.Skill;
import com.kalaconnect.model.User;
import com.kalaconnect.repository.ArtisanRepository;
import com.kalaconnect.repository.ProductRepository;
import com.kalaconnect.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtisanService {

    private final ArtisanRepository artisanRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ArtisanService(ArtisanRepository artisanRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.artisanRepository = artisanRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthorizedException("Authentication required to access artisan services");
        }
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user record not found"));
    }

    @Transactional(readOnly = true)
    public ArtisanProfileDto getProfile() {
        User user = getAuthenticatedUser();
        ArtisanProfile profile = artisanRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    ArtisanProfile newProfile = new ArtisanProfile();
                    newProfile.setUserId(user.getId());
                    newProfile.setArtisanName(user.getName());
                    newProfile.setApprovalStatus("PENDING");
                    return artisanRepository.save(newProfile);
                });

        return toDto(profile, user);
    }

    @Transactional
    public ArtisanProfileDto updateProfile(ArtisanProfileDto dto) {
        User user = getAuthenticatedUser();
        ArtisanProfile profile = artisanRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    ArtisanProfile p = new ArtisanProfile();
                    p.setUserId(user.getId());
                    return p;
                });

        if (dto.getArtisanName() != null && !dto.getArtisanName().trim().isEmpty()) {
            profile.setArtisanName(dto.getArtisanName().trim());
        }
        if (dto.getShgName() != null) {
            profile.setShgName(dto.getShgName().trim());
        }
        if (dto.getBio() != null) {
            profile.setBio(dto.getBio().trim());
        }
        if (dto.getVillageCity() != null) {
            profile.setLocation(dto.getVillageCity().trim());
        }
        if (dto.getDistrict() != null) {
            profile.setDistrict(dto.getDistrict().trim());
        }
        if (dto.getState() != null) {
            profile.setState(dto.getState().trim());
        }
        if (dto.getExperience() != null) {
            profile.setExperience(dto.getExperience());
        }
        if (dto.getContactPreference() != null) {
            profile.setContactPreference(dto.getContactPreference());
        }
        if (dto.getProfilePhoto() != null && !dto.getProfilePhoto().trim().isEmpty()) {
            profile.setProfileImage(dto.getProfilePhoto().trim());
            user.setProfileImage(dto.getProfilePhoto().trim());
            userRepository.save(user);
        }
        if (dto.getSkills() != null) {
            profile.setSkills(dto.getSkills());
        }

        ArtisanProfile saved = artisanRepository.save(profile);
        return toDto(saved, user);
    }

    @Transactional(readOnly = true)
    public List<String> getAllSkills() {
        return artisanRepository.findAllSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PagedResult<ArtisanProfileDto> getArtisans(
            String searchQuery,
            String craftCategory,
            String skill,
            String state,
            String district,
            int page,
            int size) {

        int pageSize = (size <= 0) ? 20 : Math.min(size, 100);
        int pageNumber = Math.max(page, 0);
        int offset = pageNumber * pageSize;

        long total = artisanRepository.countArtisansWithFilters(
                searchQuery, craftCategory, skill, state, district, null
        );

        List<ArtisanProfile> profiles = artisanRepository.searchArtisansWithFilters(
                searchQuery, craftCategory, skill, state, district, null, pageSize, offset
        );

        List<ArtisanProfileDto> dtos = profiles.stream()
                .map(p -> {
                    User user = (p.getUserId() != null)
                            ? userRepository.findById(p.getUserId()).orElse(null)
                            : null;
                    return toDto(p, user);
                })
                .collect(Collectors.toList());

        return PagedResult.of(dtos, pageNumber, pageSize, total);
    }

    @Transactional(readOnly = true)
    public ArtisanProfileDto getArtisanById(Long id) {
        ArtisanProfile profile = artisanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artisan not found with id: " + id));

        User user = (profile.getUserId() != null)
                ? userRepository.findById(profile.getUserId()).orElse(null)
                : null;

        ArtisanProfileDto dto = toDto(profile, user);

        if (profile.getUserId() != null) {
            List<Product> products = productRepository.findByArtisanId(profile.getUserId());
            List<ProductResponseDto> productDtos = products.stream()
                    .map(p -> {
                        ProductResponseDto pdto = new ProductResponseDto();
                        pdto.setId(p.getId());
                        pdto.setArtisanId(p.getArtisanId());
                        pdto.setArtisanName(dto.getArtisanName());
                        pdto.setShgName(dto.getShgName());
                        pdto.setArtisanProfileId(profile.getId());
                        pdto.setName(p.getName());
                        pdto.setCategory(p.getCategory());
                        pdto.setMaterial(p.getMaterial());
                        pdto.setCraftType(p.getCraftType());
                        pdto.setDescription(p.getDescription());
                        pdto.setPrice(p.getPrice());
                        pdto.setQuantity(p.getQuantity());
                        pdto.setLocation(p.getLocation());
                        pdto.setStatus(p.getStatus());
                        pdto.setAvailability((p.getQuantity() != null && p.getQuantity() > 0) ? "IN_STOCK" : "MADE_TO_ORDER");
                        pdto.setViews(p.getViewsCount());
                        pdto.setEnquiries(p.getEnquiriesCount());
                        if (p.getImages() != null && !p.getImages().isEmpty()) {
                            List<String> photoUrls = new ArrayList<>();
                            for (ProductImage img : p.getImages()) {
                                photoUrls.add(img.getImageUrl());
                            }
                            pdto.setPhotos(photoUrls);
                            pdto.setPrimaryPhoto(p.getImages().get(0).getImageUrl());
                        }
                        pdto.setCreatedAt(p.getCreatedAt());
                        pdto.setUpdatedAt(p.getUpdatedAt());
                        return pdto;
                    })
                    .collect(Collectors.toList());
            dto.setProducts(productDtos);
        }

        return dto;
    }

    private ArtisanProfileDto toDto(ArtisanProfile profile, User user) {
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
        dto.setVerified(profile.isVerified());
        dto.setSkills(profile.getSkills());
        dto.setCompletionPercentage(profile.getCompletionPercentage());
        dto.setCreatedAt(profile.getCreatedAt());
        dto.setUpdatedAt(profile.getUpdatedAt());
        return dto;
    }
}
