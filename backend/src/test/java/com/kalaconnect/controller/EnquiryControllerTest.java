package com.kalaconnect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalaconnect.dto.EnquiryRequestDto;
import com.kalaconnect.dto.StatusUpdateRequestDto;
import com.kalaconnect.model.Enquiry;
import com.kalaconnect.security.JwtAuthenticationFilter;
import com.kalaconnect.security.JwtTokenProvider;
import com.kalaconnect.service.EnquiryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnquiryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EnquiryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EnquiryService enquiryService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("POST /api/enquiries - Successfully create an enquiry")
    public void testCreateEnquiry() throws Exception {
        EnquiryRequestDto request = new EnquiryRequestDto();
        request.setProductId(1L);
        request.setArtisanId(2L);
        request.setMessage("I would like to order 5 pieces of this handicraft");
        request.setCustomerName("Sunita Devi");
        request.setCustomerEmail("sunita@example.com");
        request.setCustomerPhone("+919876543210");

        Enquiry saved = new Enquiry();
        saved.setId(10L);
        saved.setProductId(1L);
        saved.setArtisanId(2L);
        saved.setCustomerId(3L);
        saved.setMessage(request.getMessage());
        saved.setStatus("PENDING");
        saved.setCustomerName("Sunita Devi");

        when(enquiryService.createEnquiry(any(EnquiryRequestDto.class))).thenReturn(saved);

        mockMvc.perform(post("/api/enquiries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    @DisplayName("GET /api/enquiries - Retrieve enquiries for authenticated user")
    public void testGetEnquiries() throws Exception {
        Enquiry e1 = new Enquiry();
        e1.setId(1L);
        e1.setProductId(100L);
        e1.setMessage("Bulk order query");
        e1.setStatus("PENDING");

        when(enquiryService.getEnquiries(any())).thenReturn(List.of(e1));

        mockMvc.perform(get("/api/enquiries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    @DisplayName("PUT /api/enquiries/{id}/status - Update enquiry status to CONTACTED")
    public void testUpdateEnquiryStatus() throws Exception {
        StatusUpdateRequestDto updateDto = new StatusUpdateRequestDto("CONTACTED");

        Enquiry updated = new Enquiry();
        updated.setId(1L);
        updated.setStatus("CONTACTED");

        when(enquiryService.updateEnquiryStatus(eq(1L), eq("CONTACTED"))).thenReturn(updated);

        mockMvc.perform(put("/api/enquiries/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CONTACTED"));
    }
}
