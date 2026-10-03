package com.kalaconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kalaconnect.R;
import com.kalaconnect.adapters.AnalyticsBarAdapter;
import com.kalaconnect.adapters.InactiveArtisanAdapter;
import com.kalaconnect.adapters.LowViewProductAdapter;
import com.kalaconnect.adapters.OpportunityAdapter;
import com.kalaconnect.models.AnalyticsData;
import com.kalaconnect.models.AnalyticsMetricItem;
import com.kalaconnect.models.InactiveArtisan;
import com.kalaconnect.models.LowViewProduct;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.viewmodel.AnalyticsViewModel;

import java.util.List;

public class AnalyticsFragment extends Fragment {

    private AnalyticsViewModel viewModel;

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout layoutLoading;
    private LinearLayout layoutError;
    private LinearLayout layoutContent;
    private TextView tvErrorMessage;
    private Button btnRetry;

    // Overview counters
    private TextView tvTotalArtisans;
    private TextView tvTotalShgs;
    private TextView tvTotalProducts;
    private TextView tvTotalEnquiries;

    // Adapters
    private AnalyticsBarAdapter productsPerDistrictAdapter;
    private AnalyticsBarAdapter artisansPerDistrictAdapter;
    private AnalyticsBarAdapter productsByCategoryAdapter;
    private AnalyticsBarAdapter enquiriesByProductAdapter;
    private AnalyticsBarAdapter enquiriesByArtisanAdapter;
    private OpportunityAdapter skillsOpportunityAdapter;
    private OpportunityAdapter districtsOpportunityAdapter;
    private LowViewProductAdapter lowViewProductAdapter;
    private InactiveArtisanAdapter inactiveArtisanAdapter;

    // Empty views
    private TextView tvEmptyProductsPerDistrict;
    private TextView tvEmptyArtisansPerDistrict;
    private TextView tvEmptyProductsByCategory;
    private TextView tvEmptyEnquiriesByProduct;
    private TextView tvEmptyEnquiriesByArtisan;
    private TextView tvEmptySkills;
    private TextView tvEmptyLowPartDistricts;
    private TextView tvEmptyLowViews;
    private TextView tvEmptyInactiveArtisans;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analytics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerViews(view);
        initViewModel();

        viewModel.loadAnalytics();
    }

    private void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutError = view.findViewById(R.id.layoutError);
        layoutContent = view.findViewById(R.id.layoutContent);
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage);
        btnRetry = view.findViewById(R.id.btnRetry);

        tvTotalArtisans = view.findViewById(R.id.tvTotalArtisans);
        tvTotalShgs = view.findViewById(R.id.tvTotalShgs);
        tvTotalProducts = view.findViewById(R.id.tvTotalProducts);
        tvTotalEnquiries = view.findViewById(R.id.tvTotalEnquiries);

        tvEmptyProductsPerDistrict = view.findViewById(R.id.tvEmptyProductsPerDistrict);
        tvEmptyArtisansPerDistrict = view.findViewById(R.id.tvEmptyArtisansPerDistrict);
        tvEmptyProductsByCategory = view.findViewById(R.id.tvEmptyProductsByCategory);
        tvEmptyEnquiriesByProduct = view.findViewById(R.id.tvEmptyEnquiriesByProduct);
        tvEmptyEnquiriesByArtisan = view.findViewById(R.id.tvEmptyEnquiriesByArtisan);
        tvEmptySkills = view.findViewById(R.id.tvEmptySkills);
        tvEmptyLowPartDistricts = view.findViewById(R.id.tvEmptyLowPartDistricts);
        tvEmptyLowViews = view.findViewById(R.id.tvEmptyLowViews);
        tvEmptyInactiveArtisans = view.findViewById(R.id.tvEmptyInactiveArtisans);

        swipeRefreshLayout.setColorSchemeResources(R.color.primary);
        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.loadAnalytics());

        btnRetry.setOnClickListener(v -> viewModel.loadAnalytics());
    }

    private void setupRecyclerViews(View view) {
        // Products per District
        RecyclerView rvProductsPerDistrict = view.findViewById(R.id.rvProductsPerDistrict);
        rvProductsPerDistrict.setLayoutManager(new LinearLayoutManager(requireContext()));
        productsPerDistrictAdapter = new AnalyticsBarAdapter("items");
        rvProductsPerDistrict.setAdapter(productsPerDistrictAdapter);

        // Artisans per District
        RecyclerView rvArtisansPerDistrict = view.findViewById(R.id.rvArtisansPerDistrict);
        rvArtisansPerDistrict.setLayoutManager(new LinearLayoutManager(requireContext()));
        artisansPerDistrictAdapter = new AnalyticsBarAdapter("artisans");
        rvArtisansPerDistrict.setAdapter(artisansPerDistrictAdapter);

        // Products by Craft Category
        RecyclerView rvProductsByCraftCategory = view.findViewById(R.id.rvProductsByCraftCategory);
        rvProductsByCraftCategory.setLayoutManager(new LinearLayoutManager(requireContext()));
        productsByCategoryAdapter = new AnalyticsBarAdapter("products");
        rvProductsByCraftCategory.setAdapter(productsByCategoryAdapter);

        // Enquiries by Product
        RecyclerView rvEnquiriesByProduct = view.findViewById(R.id.rvEnquiriesByProduct);
        rvEnquiriesByProduct.setLayoutManager(new LinearLayoutManager(requireContext()));
        enquiriesByProductAdapter = new AnalyticsBarAdapter("enquiries");
        rvEnquiriesByProduct.setAdapter(enquiriesByProductAdapter);

        // Enquiries by Artisan
        RecyclerView rvEnquiriesByArtisan = view.findViewById(R.id.rvEnquiriesByArtisan);
        rvEnquiriesByArtisan.setLayoutManager(new LinearLayoutManager(requireContext()));
        enquiriesByArtisanAdapter = new AnalyticsBarAdapter("enquiries");
        rvEnquiriesByArtisan.setAdapter(enquiriesByArtisanAdapter);

        // Districts with Low Participation
        RecyclerView rvDistrictsLow = view.findViewById(R.id.rvDistrictsWithLowParticipation);
        rvDistrictsLow.setLayoutManager(new LinearLayoutManager(requireContext()));
        districtsOpportunityAdapter = new OpportunityAdapter("artisans");
        rvDistrictsLow.setAdapter(districtsOpportunityAdapter);

        // Skills with Low Representation
        RecyclerView rvSkillsLow = view.findViewById(R.id.rvSkillsWithLowRepresentation);
        rvSkillsLow.setLayoutManager(new LinearLayoutManager(requireContext()));
        skillsOpportunityAdapter = new OpportunityAdapter("artisans");
        rvSkillsLow.setAdapter(skillsOpportunityAdapter);

        // Products with Low Views
        RecyclerView rvProductsLowViews = view.findViewById(R.id.rvProductsWithLowViews);
        rvProductsLowViews.setLayoutManager(new LinearLayoutManager(requireContext()));
        lowViewProductAdapter = new LowViewProductAdapter();
        rvProductsLowViews.setAdapter(lowViewProductAdapter);

        // Inactive Artisans
        RecyclerView rvInactiveArtisans = view.findViewById(R.id.rvInactiveArtisans);
        rvInactiveArtisans.setLayoutManager(new LinearLayoutManager(requireContext()));
        inactiveArtisanAdapter = new InactiveArtisanAdapter();
        rvInactiveArtisans.setAdapter(inactiveArtisanAdapter);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(AnalyticsViewModel.class);

        viewModel.getAnalyticsLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            if (result.getStatus() == NetworkResult.Status.LOADING) {
                if (!swipeRefreshLayout.isRefreshing()) {
                    layoutLoading.setVisibility(View.VISIBLE);
                }
                layoutError.setVisibility(View.GONE);
            } else if (result.getStatus() == NetworkResult.Status.SUCCESS) {
                swipeRefreshLayout.setRefreshing(false);
                layoutLoading.setVisibility(View.GONE);
                layoutError.setVisibility(View.GONE);
                layoutContent.setVisibility(View.VISIBLE);

                if (result.getData() != null) {
                    populateData(result.getData());
                }
            } else if (result.getStatus() == NetworkResult.Status.ERROR) {
                swipeRefreshLayout.setRefreshing(false);
                layoutLoading.setVisibility(View.GONE);
                if (layoutContent.getVisibility() != View.VISIBLE) {
                    layoutError.setVisibility(View.VISIBLE);
                    tvErrorMessage.setText(result.getMessage() != null ? result.getMessage() : "Error loading analytics");
                }
            }
        });
    }

    private void populateData(AnalyticsData data) {
        // Summary Cards
        tvTotalArtisans.setText(String.valueOf(data.getTotalArtisans()));
        tvTotalShgs.setText(String.valueOf(data.getTotalShgs()));
        tvTotalProducts.setText(String.valueOf(data.getTotalProducts()));
        tvTotalEnquiries.setText(String.valueOf(data.getTotalEnquiries()));

        // Products per District
        List<AnalyticsMetricItem> prodDistricts = data.getProductsPerDistrict();
        productsPerDistrictAdapter.setItems(prodDistricts);
        tvEmptyProductsPerDistrict.setVisibility(prodDistricts == null || prodDistricts.isEmpty() ? View.VISIBLE : View.GONE);

        // Artisans per District
        List<AnalyticsMetricItem> artDistricts = data.getArtisansPerDistrict();
        artisansPerDistrictAdapter.setItems(artDistricts);
        tvEmptyArtisansPerDistrict.setVisibility(artDistricts == null || artDistricts.isEmpty() ? View.VISIBLE : View.GONE);

        // Products by Craft Category
        List<AnalyticsMetricItem> craftCategories = data.getProductsByCraftCategory();
        productsByCategoryAdapter.setItems(craftCategories);
        tvEmptyProductsByCategory.setVisibility(craftCategories == null || craftCategories.isEmpty() ? View.VISIBLE : View.GONE);

        // Enquiries by Product
        List<AnalyticsMetricItem> enqProduct = data.getEnquiriesByProduct();
        enquiriesByProductAdapter.setItems(enqProduct);
        tvEmptyEnquiriesByProduct.setVisibility(enqProduct == null || enqProduct.isEmpty() ? View.VISIBLE : View.GONE);

        // Enquiries by Artisan
        List<AnalyticsMetricItem> enqArtisan = data.getEnquiriesByArtisan();
        enquiriesByArtisanAdapter.setItems(enqArtisan);
        tvEmptyEnquiriesByArtisan.setVisibility(enqArtisan == null || enqArtisan.isEmpty() ? View.VISIBLE : View.GONE);

        // Districts with Low Participation
        List<AnalyticsMetricItem> lowDistricts = data.getDistrictsWithLowParticipation();
        districtsOpportunityAdapter.setItems(lowDistricts);
        tvEmptyLowPartDistricts.setVisibility(lowDistricts == null || lowDistricts.isEmpty() ? View.VISIBLE : View.GONE);

        // Skills with Low Representation
        List<AnalyticsMetricItem> lowSkills = data.getSkillsWithLowRepresentation();
        skillsOpportunityAdapter.setItems(lowSkills);
        tvEmptySkills.setVisibility(lowSkills == null || lowSkills.isEmpty() ? View.VISIBLE : View.GONE);

        // Products with Low Views
        List<LowViewProduct> lowViews = data.getProductsWithLowViews();
        lowViewProductAdapter.setItems(lowViews);
        tvEmptyLowViews.setVisibility(lowViews == null || lowViews.isEmpty() ? View.VISIBLE : View.GONE);

        // Inactive Artisans
        List<InactiveArtisan> inactiveArtisans = data.getInactiveArtisans();
        inactiveArtisanAdapter.setItems(inactiveArtisans);
        tvEmptyInactiveArtisans.setVisibility(inactiveArtisans == null || inactiveArtisans.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
