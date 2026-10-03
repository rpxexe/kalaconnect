package com.kalaconnect.service;

import com.kalaconnect.dto.EnquiryRequestDto;
import com.kalaconnect.exception.BadRequestException;
import com.kalaconnect.exception.ResourceNotFoundException;
import com.kalaconnect.model.Enquiry;
import com.kalaconnect.model.Product;
import com.kalaconnect.model.User;
import com.kalaconnect.model.UserRole;
import com.kalaconnect.repository.EnquiryRepository;
import com.kalaconnect.repository.ProductRepository;
import com.kalaconnect.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class EnquiryService {

    private static final Set<String> VALID_STATUSES = Set.of("PENDING", "CONTACTED", "RESOLVED", "CLOSED", "RESPONDED");

    private final EnquiryRepository enquiryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public EnquiryService(EnquiryRepository enquiryRepository,
                          ProductRepository productRepository,
                          UserRepository userRepository,
                          NotificationService notificationService) {
        this.enquiryRepository = enquiryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    private Optional<User> getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }
        return userRepository.findByEmail(auth.getName());
    }

    @Transactional
    public Enquiry createEnquiry(EnquiryRequestDto dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + dto.getProductId()));

        Long artisanId = dto.getArtisanId() != null ? dto.getArtisanId() : product.getArtisanId();
        Optional<User> currentUser = getAuthenticatedUser();

        Long customerId = currentUser.map(User::getId).orElse(null);
        String customerName = dto.getCustomerName();
        String customerEmail = dto.getCustomerEmail();
        String customerPhone = dto.getCustomerPhone();

        if (currentUser.isPresent()) {
            User u = currentUser.get();
            if (customerName == null || customerName.isBlank()) customerName = u.getName();
            if (customerEmail == null || customerEmail.isBlank()) customerEmail = u.getEmail();
            if (customerPhone == null || customerPhone.isBlank()) customerPhone = u.getPhone();
        }

        if (customerId == null) {
            if (customerEmail != null && !customerEmail.isBlank()) {
                customerId = userRepository.findByEmail(customerEmail)
                        .map(User::getId)
                        .orElse(null);
            }
            if (customerId == null) {
                customerId = artisanId;
            }
        }

        Enquiry enquiry = new Enquiry();
        enquiry.setProductId(product.getId());
        enquiry.setArtisanId(artisanId);
        enquiry.setCustomerId(customerId);
        enquiry.setCustomerName(customerName);
        enquiry.setCustomerEmail(customerEmail);
        enquiry.setCustomerPhone(customerPhone);
        enquiry.setMessage(dto.getMessage());
        enquiry.setStatus("PENDING");

        Enquiry saved = enquiryRepository.save(enquiry);
        saved.setProductName(product.getName());

        // 1. Notification: When an enquiry is created, create notification for the artisan
        String senderName = (customerName != null && !customerName.isBlank()) ? customerName : "A customer";
        String notificationMessage = String.format("You received an enquiry for '%s' from %s: \"%s\"",
                product.getName(), senderName, enquiry.getMessage());

        notificationService.createNotification(
                artisanId,
                "New Product Enquiry",
                notificationMessage,
                "ENQUIRY"
        );

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Enquiry> getEnquiries(String status) {
        User user = getAuthenticatedUser()
                .orElseThrow(() -> new AccessDeniedException("Authentication required to view enquiries"));

        // Security authorization check:
        // - NGO_ADMIN: can view all enquiries
        // - ARTISAN: can view only enquiries related to their products
        // - CUSTOMER: can view only their own enquiries
        if (user.getRole() == UserRole.NGO_ADMIN) {
            return enquiryRepository.findAll(status);
        } else if (user.getRole() == UserRole.ARTISAN) {
            return enquiryRepository.findByArtisanId(user.getId(), status);
        } else {
            return enquiryRepository.findByCustomerId(user.getId());
        }
    }

    @Transactional(readOnly = true)
    public List<Enquiry> getMyEnquiries() {
        return getEnquiries(null);
    }

    @Transactional(readOnly = true)
    public Enquiry getEnquiryById(Long id) {
        Enquiry enquiry = enquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enquiry not found with id: " + id));

        User user = getAuthenticatedUser()
                .orElseThrow(() -> new AccessDeniedException("Authentication required to view enquiry"));

        // Authorization check
        if (user.getRole() == UserRole.NGO_ADMIN) {
            return enquiry;
        } else if (user.getRole() == UserRole.ARTISAN) {
            if (!user.getId().equals(enquiry.getArtisanId())) {
                throw new AccessDeniedException("Artisans can view only enquiries related to their products");
            }
        } else {
            if (!user.getId().equals(enquiry.getCustomerId())) {
                throw new AccessDeniedException("Customers can view only their own enquiries");
            }
        }

        return enquiry;
    }

    @Transactional
    public Enquiry updateEnquiryStatus(Long id, String status) {
        if (status == null || status.isBlank()) {
            throw new BadRequestException("Status is required");
        }

        String normalizedStatus = status.trim().toUpperCase();
        if (!VALID_STATUSES.contains(normalizedStatus)) {
            throw new BadRequestException("Invalid status: " + status + ". Allowed: PENDING, CONTACTED, RESOLVED, CLOSED");
        }

        Enquiry enquiry = enquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enquiry not found with id: " + id));

        User user = getAuthenticatedUser()
                .orElseThrow(() -> new AccessDeniedException("Authentication required to update enquiry status"));

        // Security authorization:
        // Artisans can update only enquiries related to their products.
        // NGO Admins can update any enquiry.
        // Customers cannot update status.
        if (user.getRole() == UserRole.ARTISAN) {
            if (!user.getId().equals(enquiry.getArtisanId())) {
                throw new AccessDeniedException("Artisans can modify only enquiries related to their products");
            }
        } else if (user.getRole() != UserRole.NGO_ADMIN) {
            throw new AccessDeniedException("Customers are not authorized to update enquiry resolution status");
        }

        enquiryRepository.updateStatus(id, normalizedStatus);
        enquiry.setStatus(normalizedStatus);

        // 2. Notification: When status changes, create notification for the customer
        String productName = enquiry.getProductName() != null ? enquiry.getProductName() : "handcrafted product";
        String notificationMsg = String.format("Your enquiry for '%s' has been updated to %s by the artisan.",
                productName, normalizedStatus);

        notificationService.createNotification(
                enquiry.getCustomerId(),
                "Enquiry Status Updated",
                notificationMsg,
                "ENQUIRY"
        );

        return enquiry;
    }
}
