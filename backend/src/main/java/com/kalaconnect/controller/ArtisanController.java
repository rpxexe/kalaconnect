package com.kalaconnect.controller;

import com.kalaconnect.dto.ApiResponse;
import com.kalaconnect.dto.ArtisanProfileDto;
import com.kalaconnect.dto.PagedResult;
import com.kalaconnect.service.ArtisanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artisans")
public class ArtisanController {

    private final ArtisanService artisanService;

    public ArtisanController(ArtisanService artisanService) {
        this.artisanService = artisanService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResult<ArtisanProfileDto>>> getArtisans(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String craftCategory,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String district,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResult<ArtisanProfileDto> result = artisanService.getArtisans(search, craftCategory, skill, state, district, page, size);
        return ResponseEntity.ok(ApiResponse.success("Artisans retrieved successfully", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArtisanProfileDto>> getArtisanById(@PathVariable Long id) {
        ArtisanProfileDto profile = artisanService.getArtisanById(id);
        return ResponseEntity.ok(ApiResponse.success("Artisan profile retrieved successfully", profile));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ArtisanProfileDto>> getProfile() {
        ArtisanProfileDto profile = artisanService.getProfile();
        return ResponseEntity.ok(ApiResponse.success("Artisan profile retrieved successfully", profile));
    }

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<ArtisanProfileDto>> createProfile(@RequestBody ArtisanProfileDto request) {
        ArtisanProfileDto profile = artisanService.updateProfile(request);
        return ResponseEntity.ok(ApiResponse.success("Artisan profile created/saved successfully", profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<ArtisanProfileDto>> updateProfile(@RequestBody ArtisanProfileDto request) {
        ArtisanProfileDto profile = artisanService.updateProfile(request);
        return ResponseEntity.ok(ApiResponse.success("Artisan profile updated successfully", profile));
    }

    @GetMapping("/skills")
    public ResponseEntity<ApiResponse<List<String>>> getSkills() {
        List<String> skills = artisanService.getAllSkills();
        return ResponseEntity.ok(ApiResponse.success("Available skills retrieved successfully", skills));
    }
}
