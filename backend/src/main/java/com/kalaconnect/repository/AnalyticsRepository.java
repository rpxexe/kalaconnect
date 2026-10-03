package com.kalaconnect.repository;

import com.kalaconnect.dto.AnalyticsMetricItemDto;
import com.kalaconnect.dto.InactiveArtisanDto;
import com.kalaconnect.dto.LowViewProductDto;

import java.util.List;

public interface AnalyticsRepository {

    long countTotalArtisans();

    long countTotalShgs();

    long countTotalProducts();

    long countTotalEnquiries();

    List<AnalyticsMetricItemDto> getProductsPerDistrict(int limit);

    List<AnalyticsMetricItemDto> getArtisansPerDistrict(int limit);

    List<AnalyticsMetricItemDto> getProductsByCraftCategory(int limit);

    List<AnalyticsMetricItemDto> getEnquiriesByProduct(int limit);

    List<AnalyticsMetricItemDto> getEnquiriesByArtisan(int limit);

    List<LowViewProductDto> getProductsWithLowViews(int limit);

    List<InactiveArtisanDto> getInactiveArtisans(int limit);

    List<AnalyticsMetricItemDto> getDistrictsWithLowParticipation(int limit);

    List<AnalyticsMetricItemDto> getSkillsWithLowRepresentation(int limit);
}
