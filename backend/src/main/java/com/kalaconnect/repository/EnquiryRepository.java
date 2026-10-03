package com.kalaconnect.repository;

import com.kalaconnect.model.Enquiry;

import java.util.List;
import java.util.Optional;

public interface EnquiryRepository {

    Enquiry save(Enquiry enquiry);

    Optional<Enquiry> findById(Long id);

    List<Enquiry> findAll(String status);

    List<Enquiry> findByArtisanId(Long artisanId, String status);

    List<Enquiry> findByCustomerId(Long customerId);

    void updateStatus(Long id, String status);
}
