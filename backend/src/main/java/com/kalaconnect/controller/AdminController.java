package com.kalaconnect.controller;

import com.kalaconnect.dto.AdminActionRequestDto;
import com.kalaconnect.dto.AdminArtisanUpdateDto;
import com.kalaconnect.dto.AdminDashboardMetricsDto;
import com.kalaconnect.dto.ApiResponse;
import com.kalaconnect.dto.ArtisanProfileDto;
import com.kalaconnect.dto.PagedResult;
import com.kalaconnect.dto.ProductResponseDto;
import com.kalaconnect.model.Enquiry;
import com.kalaconnect.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping({"/api/admin", "/api/v1/admin"})
@PreAuthorize("hasRole('NGO_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardMetricsDto>> getDashboardMetrics() {
        AdminDashboardMetricsDto metrics = adminService.getDashboardMetrics();
        return ResponseEntity.ok(ApiResponse.success(metrics));
    }

    @GetMapping("/artisans")
    public ResponseEntity<ApiResponse<PagedResult<ArtisanProfileDto>>> getArtisans(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResult<ArtisanProfileDto> result = adminService.getArtisans(
                search, category, skill, state, district, status, page, size
        );
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/artisans/{id}/approve")
    public ResponseEntity<ApiResponse<ArtisanProfileDto>> approveArtisan(
            @PathVariable Long id,
            @RequestBody(required = false) AdminActionRequestDto request,
            Authentication authentication) {

        String adminEmail = (authentication != null) ? authentication.getName() : null;
        String reason = (request != null) ? request.getReason() : "Approved by NGO Admin";

        ArtisanProfileDto updated = adminService.approveArtisan(id, adminEmail, reason);
        return ResponseEntity.ok(ApiResponse.success("Artisan verified and approved successfully", updated));
    }

    @PutMapping("/artisans/{id}/reject")
    public ResponseEntity<ApiResponse<ArtisanProfileDto>> rejectArtisan(
            @PathVariable Long id,
            @RequestBody(required = false) AdminActionRequestDto request,
            Authentication authentication) {

        String adminEmail = (authentication != null) ? authentication.getName() : null;
        String reason = (request != null) ? request.getReason() : "Rejected by NGO Admin";

        ArtisanProfileDto updated = adminService.rejectArtisan(id, adminEmail, reason);
        return ResponseEntity.ok(ApiResponse.success("Artisan status updated to rejected", updated));
    }

    @PutMapping("/artisans/{id}")
    public ResponseEntity<ApiResponse<ArtisanProfileDto>> updateArtisan(
            @PathVariable Long id,
            @RequestBody AdminArtisanUpdateDto updateDto,
            Authentication authentication) {

        String adminEmail = (authentication != null) ? authentication.getName() : null;
        ArtisanProfileDto updated = adminService.updateArtisan(id, updateDto, adminEmail);
        return ResponseEntity.ok(ApiResponse.success("Artisan details updated successfully", updated));
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<PagedResult<ProductResponseDto>>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String craftType,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResult<ProductResponseDto> result = adminService.getProducts(
                search, category, craftType, state, district, minPrice, maxPrice, status, page, size
        );
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/enquiries")
    public ResponseEntity<ApiResponse<List<Enquiry>>> getEnquiries(
            @RequestParam(required = false) String status) {

        List<Enquiry> enquiries = adminService.getEnquiries(status);
        return ResponseEntity.ok(ApiResponse.success(enquiries));
    }
}
