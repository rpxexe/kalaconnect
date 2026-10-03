package com.kalaconnect.service;

import com.kalaconnect.dto.AdminArtisanUpdateDto;
import com.kalaconnect.dto.AdminDashboardMetricsDto;
import com.kalaconnect.dto.ArtisanProfileDto;
import com.kalaconnect.dto.PagedResult;
import com.kalaconnect.dto.ProductResponseDto;
import com.kalaconnect.model.Enquiry;

import java.math.BigDecimal;
import java.util.List;

public interface AdminService {

    AdminDashboardMetricsDto getDashboardMetrics();

    PagedResult<ArtisanProfileDto> getArtisans(
            String searchQuery,
            String craftCategory,
            String skill,
            String state,
            String district,
            String approvalStatus,
            int page,
            int size
    );

    ArtisanProfileDto approveArtisan(Long artisanId, String adminEmail, String reason);

    ArtisanProfileDto rejectArtisan(Long artisanId, String adminEmail, String reason);

    ArtisanProfileDto updateArtisan(Long artisanId, AdminArtisanUpdateDto updateDto, String adminEmail);

    PagedResult<ProductResponseDto> getProducts(
            String searchQuery,
            String category,
            String craftType,
            String state,
            String district,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String status,
            int page,
            int size
    );

    List<Enquiry> getEnquiries(String status);
}
