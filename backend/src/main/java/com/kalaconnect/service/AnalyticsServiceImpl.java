package com.kalaconnect.service;

import com.kalaconnect.dto.AnalyticsDashboardResponseDto;
import com.kalaconnect.dto.AnalyticsMetricItemDto;
import com.kalaconnect.repository.AnalyticsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public AnalyticsServiceImpl(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    @Override
    public AnalyticsDashboardResponseDto getAnalyticsDashboard() {
        AnalyticsDashboardResponseDto dashboard = new AnalyticsDashboardResponseDto();

        long totalArtisans = analyticsRepository.countTotalArtisans();
        long totalShgs = analyticsRepository.countTotalShgs();
        long totalProducts = analyticsRepository.countTotalProducts();
        long totalEnquiries = analyticsRepository.countTotalEnquiries();

        dashboard.setTotalArtisans(totalArtisans);
        dashboard.setTotalShgs(totalShgs);
        dashboard.setTotalProducts(totalProducts);
        dashboard.setTotalEnquiries(totalEnquiries);

        List<AnalyticsMetricItemDto> productsPerDistrict = analyticsRepository.getProductsPerDistrict(10);
        calculatePercentages(productsPerDistrict, totalProducts);
        dashboard.setProductsPerDistrict(productsPerDistrict);

        List<AnalyticsMetricItemDto> artisansPerDistrict = analyticsRepository.getArtisansPerDistrict(10);
        calculatePercentages(artisansPerDistrict, totalArtisans);
        dashboard.setArtisansPerDistrict(artisansPerDistrict);

        List<AnalyticsMetricItemDto> productsByCraftCategory = analyticsRepository.getProductsByCraftCategory(10);
        calculatePercentages(productsByCraftCategory, totalProducts);
        dashboard.setProductsByCraftCategory(productsByCraftCategory);

        List<AnalyticsMetricItemDto> enquiriesByProduct = analyticsRepository.getEnquiriesByProduct(10);
        calculatePercentages(enquiriesByProduct, totalEnquiries);
        dashboard.setEnquiriesByProduct(enquiriesByProduct);

        List<AnalyticsMetricItemDto> enquiriesByArtisan = analyticsRepository.getEnquiriesByArtisan(10);
        calculatePercentages(enquiriesByArtisan, totalEnquiries);
        dashboard.setEnquiriesByArtisan(enquiriesByArtisan);

        dashboard.setProductsWithLowViews(analyticsRepository.getProductsWithLowViews(10));
        dashboard.setInactiveArtisans(analyticsRepository.getInactiveArtisans(10));

        List<AnalyticsMetricItemDto> lowPartDistricts = analyticsRepository.getDistrictsWithLowParticipation(10);
        dashboard.setDistrictsWithLowParticipation(lowPartDistricts);

        List<AnalyticsMetricItemDto> lowRepSkills = analyticsRepository.getSkillsWithLowRepresentation(10);
        dashboard.setSkillsWithLowRepresentation(lowRepSkills);

        return dashboard;
    }

    private void calculatePercentages(List<AnalyticsMetricItemDto> items, long total) {
        if (items == null || items.isEmpty()) {
            return;
        }
        long basis = total > 0 ? total : items.stream().mapToLong(AnalyticsMetricItemDto::getCount).sum();
        if (basis <= 0) {
            for (AnalyticsMetricItemDto item : items) {
                item.setPercentage(0.0);
            }
            return;
        }
        for (AnalyticsMetricItemDto item : items) {
            double pct = (item.getCount() * 100.0) / basis;
            item.setPercentage(Math.round(pct * 10.0) / 10.0);
        }
    }
}
