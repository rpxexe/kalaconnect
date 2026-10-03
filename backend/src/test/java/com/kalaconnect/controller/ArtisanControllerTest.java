package com.kalaconnect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalaconnect.dto.ArtisanProfileDto;
import com.kalaconnect.security.JwtAuthenticationFilter;
import com.kalaconnect.security.JwtTokenProvider;
import com.kalaconnect.service.ArtisanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArtisanController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ArtisanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArtisanService artisanService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetProfile_Success() throws Exception {
        ArtisanProfileDto profile = new ArtisanProfileDto();
        profile.setId(1L);
        profile.setUserId(10L);
        profile.setArtisanName("Meera Bai");
        profile.setShgName("Shakti SHG");
        profile.setSkills(Arrays.asList("Pottery", "Painting"));
        profile.setCompletionPercentage(80);

        when(artisanService.getProfile()).thenReturn(profile);

        mockMvc.perform(get("/api/artisans/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.artisanName").value("Meera Bai"))
                .andExpect(jsonPath("$.data.completionPercentage").value(80));
    }

    @Test
    public void testUpdateProfile_Success() throws Exception {
        ArtisanProfileDto request = new ArtisanProfileDto();
        request.setArtisanName("Meera Bai");
        request.setShgName("Shakti SHG");
        request.setVillageCity("Jaipur");
        request.setSkills(Arrays.asList("Pottery", "Woodcraft"));

        ArtisanProfileDto updated = new ArtisanProfileDto();
        updated.setId(1L);
        updated.setArtisanName("Meera Bai");
        updated.setVillageCity("Jaipur");
        updated.setCompletionPercentage(90);

        when(artisanService.updateProfile(any(ArtisanProfileDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/artisans/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.villageCity").value("Jaipur"));
    }

    @Test
    public void testGetSkills_Success() throws Exception {
        when(artisanService.getAllSkills()).thenReturn(Arrays.asList("Pottery", "Weaving", "Embroidery"));

        mockMvc.perform(get("/api/artisans/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    @Test
    public void testGetArtisans_Success() throws Exception {
        ArtisanProfileDto p = new ArtisanProfileDto();
        p.setId(1L);
        p.setArtisanName("Meera Bai");
        com.kalaconnect.dto.PagedResult<ArtisanProfileDto> page = com.kalaconnect.dto.PagedResult.of(
                Collections.singletonList(p), 0, 20, 1
        );

        when(artisanService.getArtisans(any(), any(), any(), any(), any(), eq(0), eq(20)))
                .thenReturn(page);

        mockMvc.perform(get("/api/artisans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items[0].id").value(1))
                .andExpect(jsonPath("$.data.items[0].artisanName").value("Meera Bai"));
    }

    @Test
    public void testGetArtisanById_Success() throws Exception {
        ArtisanProfileDto p = new ArtisanProfileDto();
        p.setId(1L);
        p.setArtisanName("Meera Bai");
        p.setBio("Master potter");

        when(artisanService.getArtisanById(eq(1L))).thenReturn(p);

        mockMvc.perform(get("/api/artisans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.bio").value("Master potter"));
    }
}
