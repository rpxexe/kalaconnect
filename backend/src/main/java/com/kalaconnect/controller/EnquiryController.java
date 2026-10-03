package com.kalaconnect.controller;

import com.kalaconnect.dto.ApiResponse;
import com.kalaconnect.dto.EnquiryRequestDto;
import com.kalaconnect.dto.StatusUpdateRequestDto;
import com.kalaconnect.model.Enquiry;
import com.kalaconnect.service.EnquiryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enquiries")
public class EnquiryController {

    private final EnquiryService enquiryService;

    public EnquiryController(EnquiryService enquiryService) {
        this.enquiryService = enquiryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Enquiry>> createEnquiry(@Valid @RequestBody EnquiryRequestDto request) {
        Enquiry enquiry = enquiryService.createEnquiry(request);
        return new ResponseEntity<>(ApiResponse.success("Enquiry submitted successfully", enquiry), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Enquiry>>> getEnquiries(
            @RequestParam(required = false) String status) {
        List<Enquiry> list = enquiryService.getEnquiries(status);
        return ResponseEntity.ok(ApiResponse.success("Enquiries retrieved successfully", list));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<Enquiry>>> getMyEnquiries() {
        List<Enquiry> list = enquiryService.getMyEnquiries();
        return ResponseEntity.ok(ApiResponse.success("Enquiries retrieved successfully", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Enquiry>> getEnquiryById(@PathVariable Long id) {
        Enquiry enquiry = enquiryService.getEnquiryById(id);
        return ResponseEntity.ok(ApiResponse.success("Enquiry retrieved successfully", enquiry));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Enquiry>> updateEnquiryStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequestDto request) {
        Enquiry updated = enquiryService.updateEnquiryStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Enquiry status updated successfully", updated));
    }
}
