package com.kalaconnect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalaconnect.dto.AdminDashboardMetricsDto;
import com.kalaconnect.dto.ArtisanProfileDto;
import com.kalaconnect.dto.PagedResult;
import com.kalaconnect.dto.ProductResponseDto;
import com.kalaconnect.model.Enquiry;
import com.kalaconnect.security.JwtAuthenticationFilter;
import com.kalaconnect.security.JwtTokenProvider;
import com.kalaconnect.service.AdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/admin/dashboard - Should return dashboard metrics")
    void testGetDashboardMetrics() throws Exception {
        AdminDashboardMetricsDto metrics = new AdminDashboardMetricsDto(12, 45, 80, 25, 4, 38);
        metrics.setPendingEnquiries(7);
        metrics.setContactedEnquiries(10);
        metrics.setResolvedEnquiries(5);
        metrics.setClosedEnquiries(3);

        Mockito.when(adminService.getDashboardMetrics()).thenReturn(metrics);

        mockMvc.perform(get("/api/admin/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalShgs", is(12)))
                .andExpect(jsonPath("$.data.totalArtisans", is(45)))
                .andExpect(jsonPath("$.data.totalProducts", is(80)))
                .andExpect(jsonPath("$.data.totalEnquiries", is(25)))
                .andExpect(jsonPath("$.data.pendingApprovals", is(4)))
                .andExpect(jsonPath("$.data.activeArtisans", is(38)));
    }

    @Test
    @DisplayName("GET /api/admin/artisans - Should return paginated artisan list")
    void testGetArtisans() throws Exception {
        ArtisanProfileDto artisan = new ArtisanProfileDto();
        artisan.setId(1L);
        artisan.setArtisanName("Ramesh Kumar");
        artisan.setShgName("Varanasi Handloom SHG");
        artisan.setApprovalStatus("PENDING");

        PagedResult<ArtisanProfileDto> result = PagedResult.of(List.of(artisan), 0, 20, 1L);
        Mockito.when(adminService.getArtisans(any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(result);

        mockMvc.perform(get("/api/admin/artisans?status=PENDING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.items[0].artisanName", is("Ramesh Kumar")))
                .andExpect(jsonPath("$.data.items[0].approvalStatus", is("PENDING")));
    }

    @Test
    @DisplayName("PUT /api/admin/artisans/1/approve - Should approve artisan")
    void testApproveArtisan() throws Exception {
        ArtisanProfileDto approved = new ArtisanProfileDto();
        approved.setId(1L);
        approved.setArtisanName("Ramesh Kumar");
        approved.setApprovalStatus("APPROVED");
        approved.setVerified(true);

        Mockito.when(adminService.approveArtisan(eq(1L), any(), any()))
                .thenReturn(approved);

        mockMvc.perform(put("/api/admin/artisans/1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"Credentials verified successfully\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.approvalStatus", is("APPROVED")))
                .andExpect(jsonPath("$.data.verified", is(true)));
    }

    @Test
    @DisplayName("PUT /api/admin/artisans/1/reject - Should reject artisan")
    void testRejectArtisan() throws Exception {
        ArtisanProfileDto rejected = new ArtisanProfileDto();
        rejected.setId(1L);
        rejected.setArtisanName("Ramesh Kumar");
        rejected.setApprovalStatus("REJECTED");
        rejected.setVerified(false);

        Mockito.when(adminService.rejectArtisan(eq(1L), any(), any()))
                .thenReturn(rejected);

        mockMvc.perform(put("/api/admin/artisans/1/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"Incomplete documentation\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.approvalStatus", is("REJECTED")))
                .andExpect(jsonPath("$.data.verified", is(false)));
    }

    @Test
    @DisplayName("GET /api/admin/products - Should return paginated products")
    void testGetProducts() throws Exception {
        ProductResponseDto product = new ProductResponseDto();
        product.setId(10L);
        product.setName("Terracotta Planter");
        product.setCategory("Pottery");
        product.setPrice(new BigDecimal("650.00"));
        product.setStatus("AVAILABLE");

        PagedResult<ProductResponseDto> pagedResult = PagedResult.of(List.of(product), 0, 20, 1L);
        Mockito.when(adminService.getProducts(any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(pagedResult);

        mockMvc.perform(get("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.items[0].name", is("Terracotta Planter")));
    }

    @Test
    @DisplayName("GET /api/admin/enquiries - Should return enquiries")
    void testGetEnquiries() throws Exception {
        Enquiry enquiry = new Enquiry();
        enquiry.setId(100L);
        enquiry.setCustomerName("Pooja Mishra");
        enquiry.setMessage("Inquiry regarding bulk order");
        enquiry.setStatus("PENDING");

        Mockito.when(adminService.getEnquiries("PENDING")).thenReturn(List.of(enquiry));

        mockMvc.perform(get("/api/admin/enquiries?status=PENDING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data[0].customerName", is("Pooja Mishra")))
                .andExpect(jsonPath("$.data[0].status", is("PENDING")));
    }
}
