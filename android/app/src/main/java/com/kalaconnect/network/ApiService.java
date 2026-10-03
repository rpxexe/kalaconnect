package com.kalaconnect.network;

import com.kalaconnect.models.AiProductContentRequest;
import com.kalaconnect.models.AiProductContentResponse;
import com.kalaconnect.models.ApiResponse;
import com.kalaconnect.models.ArtisanProfile;
import com.kalaconnect.models.AuthResponse;
import com.kalaconnect.models.LoginRequest;
import com.kalaconnect.models.Product;
import com.kalaconnect.models.ProductRequest;
import com.kalaconnect.models.RegisterRequest;
import com.kalaconnect.models.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);

    @GET("api/auth/me")
    Call<ApiResponse<User>> getCurrentUser();

    @GET("api/health/db")
    Call<Map<String, String>> checkDatabaseHealth();

    @GET("api/health")
    Call<Map<String, Object>> checkHealth();

    // Artisan Profile
    @GET("api/artisans/profile")
    Call<ApiResponse<ArtisanProfile>> getArtisanProfile();

    @POST("api/artisans/profile")
    Call<ApiResponse<ArtisanProfile>> createArtisanProfile(@Body ArtisanProfile profile);

    @PUT("api/artisans/profile")
    Call<ApiResponse<ArtisanProfile>> updateArtisanProfile(@Body ArtisanProfile profile);

    @GET("api/artisans/skills")
    Call<ApiResponse<List<String>>> getAvailableSkills();

    // Products
    @POST("api/products")
    Call<ApiResponse<Product>> createProduct(@Body ProductRequest product);

    @GET("api/products/my")
    Call<ApiResponse<List<Product>>> getMyProducts();

    @GET("api/products/{id}")
    Call<ApiResponse<Product>> getProductById(@Path("id") Long id);

    @PUT("api/products/{id}")
    Call<ApiResponse<Product>> updateProduct(@Path("id") Long id, @Body ProductRequest product);

    @DELETE("api/products/{id}")
    Call<ApiResponse<Void>> deleteProduct(@Path("id") Long id);

    @PUT("api/products/{id}/status")
    Call<ApiResponse<Product>> updateProductStatus(@Path("id") Long id, @Body Map<String, String> statusBody);

    // AI Product Promotion Content Generation
    @POST("api/ai/product-content")
    Call<AiProductContentResponse> generateAiProductContent(@Body AiProductContentRequest request);

    // Customer Browsing - Products (Paginated)
    @GET("api/products")
    Call<ApiResponse<com.kalaconnect.models.PagedResult<Product>>> getProducts(
            @retrofit2.http.Query("search") String search,
            @retrofit2.http.Query("category") String category,
            @retrofit2.http.Query("craftType") String craftType,
            @retrofit2.http.Query("state") String state,
            @retrofit2.http.Query("district") String district,
            @retrofit2.http.Query("minPrice") java.math.BigDecimal minPrice,
            @retrofit2.http.Query("maxPrice") java.math.BigDecimal maxPrice,
            @retrofit2.http.Query("page") int page,
            @retrofit2.http.Query("size") int size
    );

    // Customer Browsing - Artisans (Paginated)
    @GET("api/artisans")
    Call<ApiResponse<com.kalaconnect.models.PagedResult<ArtisanProfile>>> getArtisans(
            @retrofit2.http.Query("search") String search,
            @retrofit2.http.Query("craftCategory") String craftCategory,
            @retrofit2.http.Query("skill") String skill,
            @retrofit2.http.Query("state") String state,
            @retrofit2.http.Query("district") String district,
            @retrofit2.http.Query("page") int page,
            @retrofit2.http.Query("size") int size
    );

    @GET("api/artisans/{id}")
    Call<ApiResponse<ArtisanProfile>> getArtisanById(@Path("id") Long id);

    // Customer Enquiries
    @POST("api/enquiries")
    Call<ApiResponse<com.kalaconnect.models.EnquiryItem>> sendEnquiry(@Body com.kalaconnect.models.EnquiryRequest request);

    @GET("api/enquiries")
    Call<ApiResponse<List<com.kalaconnect.models.EnquiryItem>>> getEnquiries(@retrofit2.http.Query("status") String status);

    @GET("api/enquiries/my")
    Call<ApiResponse<List<com.kalaconnect.models.EnquiryItem>>> getMyEnquiries();

    @GET("api/enquiries/{id}")
    Call<ApiResponse<com.kalaconnect.models.EnquiryItem>> getEnquiryById(@Path("id") Long id);

    @PUT("api/enquiries/{id}/status")
    Call<ApiResponse<com.kalaconnect.models.EnquiryItem>> updateEnquiryStatus(
            @Path("id") Long id,
            @Body com.kalaconnect.models.StatusUpdateRequest request
    );

    // Notifications
    @GET("api/notifications")
    Call<ApiResponse<List<com.kalaconnect.models.NotificationItem>>> getNotifications(
            @retrofit2.http.Query("unreadOnly") Boolean unreadOnly
    );

    @PUT("api/notifications/{id}/read")
    Call<ApiResponse<Void>> markNotificationAsRead(@Path("id") Long id);

    // NGO Admin Endpoints
    @GET("api/admin/dashboard")
    Call<ApiResponse<com.kalaconnect.models.AdminDashboardMetrics>> getAdminDashboard();

    @GET("api/admin/artisans")
    Call<ApiResponse<com.kalaconnect.models.PagedResult<ArtisanProfile>>> getAdminArtisans(
            @retrofit2.http.Query("search") String search,
            @retrofit2.http.Query("status") String status,
            @retrofit2.http.Query("page") int page,
            @retrofit2.http.Query("size") int size
    );

    @PUT("api/admin/artisans/{id}/approve")
    Call<ApiResponse<ArtisanProfile>> approveArtisan(
            @Path("id") Long id,
            @Body com.kalaconnect.models.AdminActionRequest request
    );

    @PUT("api/admin/artisans/{id}/reject")
    Call<ApiResponse<ArtisanProfile>> rejectArtisan(
            @Path("id") Long id,
            @Body com.kalaconnect.models.AdminActionRequest request
    );

    @GET("api/admin/products")
    Call<ApiResponse<com.kalaconnect.models.PagedResult<Product>>> getAdminProducts(
            @retrofit2.http.Query("search") String search,
            @retrofit2.http.Query("category") String category,
            @retrofit2.http.Query("status") String status,
            @retrofit2.http.Query("page") int page,
            @retrofit2.http.Query("size") int size
    );

    @GET("api/admin/enquiries")
    Call<ApiResponse<List<com.kalaconnect.models.EnquiryItem>>> getAdminEnquiries(
            @retrofit2.http.Query("status") String status
    );

    // Analytics
    @GET("api/analytics")
    Call<ApiResponse<com.kalaconnect.models.AnalyticsData>> getAnalytics();
}
