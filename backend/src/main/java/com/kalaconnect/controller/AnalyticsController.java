package com.kalaconnect.controller;

import com.kalaconnect.dto.AnalyticsDashboardResponseDto;
import com.kalaconnect.dto.ApiResponse;
import com.kalaconnect.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping({"/api/analytics", "/api/v1/analytics", "/api/admin/analytics", "/api/v1/admin/analytics"})
    public ResponseEntity<ApiResponse<AnalyticsDashboardResponseDto>> getAnalyticsDashboard() {
        AnalyticsDashboardResponseDto analytics = analyticsService.getAnalyticsDashboard();
        return ResponseEntity.ok(ApiResponse.success(analytics));
    }
}
