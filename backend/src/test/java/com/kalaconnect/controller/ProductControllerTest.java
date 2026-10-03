package com.kalaconnect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalaconnect.dto.ProductRequestDto;
import com.kalaconnect.dto.ProductResponseDto;
import com.kalaconnect.security.JwtAuthenticationFilter;
import com.kalaconnect.security.JwtTokenProvider;
import com.kalaconnect.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateProduct_Success() throws Exception {
        ProductRequestDto request = new ProductRequestDto();
        request.setName("Terracotta Vase");
        request.setCategory("Pottery");
        request.setDescription("Handmade earthen vase");
        request.setPrice(new BigDecimal("850.00"));
        request.setQuantity(5);

        ProductResponseDto response = new ProductResponseDto();
        response.setId(101L);
        response.setName("Terracotta Vase");
        response.setPrice(new BigDecimal("850.00"));
        response.setStatus("PUBLISHED");

        when(productService.createProduct(any(ProductRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(101))
                .andExpect(jsonPath("$.data.name").value("Terracotta Vase"));
    }

    @Test
    public void testGetMyProducts_Success() throws Exception {
        ProductResponseDto p = new ProductResponseDto();
        p.setId(101L);
        p.setName("Terracotta Vase");
        p.setViews(12);
        p.setEnquiries(3);

        when(productService.getMyProducts()).thenReturn(Collections.singletonList(p));

        mockMvc.perform(get("/api/products/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(101));
    }

    @Test
    public void testDeleteProduct_Success() throws Exception {
        doNothing().when(productService).deleteProduct(eq(101L));

        mockMvc.perform(delete("/api/products/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testGetProducts_Success() throws Exception {
        ProductResponseDto p = new ProductResponseDto();
        p.setId(101L);
        p.setName("Terracotta Vase");
        p.setCategory("Pottery");
        com.kalaconnect.dto.PagedResult<ProductResponseDto> page = com.kalaconnect.dto.PagedResult.of(
                Collections.singletonList(p), 0, 20, 1
        );

        when(productService.getProducts(any(), any(), any(), any(), any(), any(), any(), eq(0), eq(20)))
                .thenReturn(page);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items[0].id").value(101))
                .andExpect(jsonPath("$.data.totalItems").value(1));
    }
}
