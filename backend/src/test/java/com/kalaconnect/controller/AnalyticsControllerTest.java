package com.kalaconnect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalaconnect.dto.AnalyticsDashboardResponseDto;
import com.kalaconnect.dto.AnalyticsMetricItemDto;
import com.kalaconnect.dto.InactiveArtisanDto;
import com.kalaconnect.dto.LowViewProductDto;
import com.kalaconnect.security.JwtAuthenticationFilter;
import com.kalaconnect.security.JwtTokenProvider;
import com.kalaconnect.service.AnalyticsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalyticsController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/analytics - Should return full analytics dashboard")
    void testGetAnalyticsDashboard() throws Exception {
        AnalyticsDashboardResponseDto dto = new AnalyticsDashboardResponseDto();
        dto.setTotalArtisans(148);
        dto.setTotalShgs(32);
        dto.setTotalProducts(426);
        dto.setTotalEnquiries(115);

        dto.setProductsPerDistrict(List.of(
                new AnalyticsMetricItemDto("Varanasi", 120, 28.2),
                new AnalyticsMetricItemDto("Jaipur", 95, 22.3)
        ));

        dto.setArtisansPerDistrict(List.of(
                new AnalyticsMetricItemDto("Varanasi", 45, 30.4),
                new AnalyticsMetricItemDto("Jaipur", 35, 23.6)
        ));

        dto.setProductsByCraftCategory(List.of(
                new AnalyticsMetricItemDto("Pottery", 150, 35.2),
                new AnalyticsMetricItemDto("Weaving", 110, 25.8)
        ));

        dto.setEnquiriesByProduct(List.of(
                new AnalyticsMetricItemDto("Blue Pottery Vase", 28, 24.3)
        ));

        dto.setEnquiriesByArtisan(List.of(
                new AnalyticsMetricItemDto("Kailash Chand", 34, 29.6)
        ));

        dto.setProductsWithLowViews(List.of(
                new LowViewProductDto(1L, "Handwoven Rug", "Weaving", "Ramesh", 3)
        ));

        dto.setInactiveArtisans(List.of(
                new InactiveArtisanDto(2L, "Sunita Devi", "Rural Weavers", "Mirzapur", 0)
        ));

        dto.setDistrictsWithLowParticipation(List.of(
                new AnalyticsMetricItemDto("Mirzapur", 1, 0.7)
        ));

        dto.setSkillsWithLowRepresentation(List.of(
                new AnalyticsMetricItemDto("Bamboo craft", 2, 1.4)
        ));

        Mockito.when(analyticsService.getAnalyticsDashboard()).thenReturn(dto);

        mockMvc.perform(get("/api/analytics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalArtisans", is(148)))
                .andExpect(jsonPath("$.data.totalShgs", is(32)))
                .andExpect(jsonPath("$.data.totalProducts", is(426)))
                .andExpect(jsonPath("$.data.totalEnquiries", is(115)))
                .andExpect(jsonPath("$.data.productsPerDistrict[0].label", is("Varanasi")))
                .andExpect(jsonPath("$.data.productsPerDistrict[0].count", is(120)))
                .andExpect(jsonPath("$.data.productsWithLowViews[0].name", is("Handwoven Rug")))
                .andExpect(jsonPath("$.data.inactiveArtisans[0].artisanName", is("Sunita Devi")))
                .andExpect(jsonPath("$.data.districtsWithLowParticipation[0].label", is("Mirzapur")))
                .andExpect(jsonPath("$.data.skillsWithLowRepresentation[0].label", is("Bamboo craft")));
    }

    @Test
    @DisplayName("GET /api/admin/analytics - Should return full analytics dashboard for admin")
    void testGetAdminAnalyticsDashboard() throws Exception {
        AnalyticsDashboardResponseDto dto = new AnalyticsDashboardResponseDto();
        dto.setTotalArtisans(148);
        dto.setTotalShgs(32);
        dto.setTotalProducts(426);
        dto.setTotalEnquiries(115);

        Mockito.when(analyticsService.getAnalyticsDashboard()).thenReturn(dto);

        mockMvc.perform(get("/api/admin/analytics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalArtisans", is(148)));
    }
}
