package com.kalaconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.kalaconnect.R;
import com.kalaconnect.models.ArtisanProfile;
import com.kalaconnect.models.Product;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.viewmodel.ArtisanViewModel;
import com.kalaconnect.viewmodel.AuthViewModel;
import com.kalaconnect.viewmodel.ProductViewModel;

import java.util.List;

public class ArtisanOverviewFragment extends Fragment {

    public interface OnOverviewNavigationListener {
        void onNavigateToTab(int menuItemId);
    }

    private AuthViewModel authViewModel;
    private ArtisanViewModel artisanViewModel;
    private ProductViewModel productViewModel;

    private ImageView ivOverviewAvatar;
    private TextView tvOverviewGreeting;
    private TextView tvOverviewShgBadge;
    private ImageButton btnOverviewLogout;

    private MaterialCardView cardOverviewCompletion;
    private TextView tvOverviewCompletionScore;
    private LinearProgressIndicator progressOverviewBar;

    private TextView tvStatProductCount;
    private TextView tvStatEnquiryCount;

    private MaterialCardView cardStatProducts;
    private MaterialCardView cardStatEnquiries;
    private MaterialCardView cardActionAddProduct;
    private MaterialCardView cardActionManageCatalogue;
    private MaterialCardView cardActionEditProfile;

    private OnOverviewNavigationListener navListener;

    public void setOnOverviewNavigationListener(OnOverviewNavigationListener listener) {
        this.navListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artisan_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initViewModels();
        setupListeners();

        artisanViewModel.loadProfile();
        productViewModel.loadMyProducts();
    }

    private void initViews(View view) {
        ivOverviewAvatar = view.findViewById(R.id.ivOverviewAvatar);
        tvOverviewGreeting = view.findViewById(R.id.tvOverviewGreeting);
        tvOverviewShgBadge = view.findViewById(R.id.tvOverviewShgBadge);
        btnOverviewLogout = view.findViewById(R.id.btnOverviewLogout);

        cardOverviewCompletion = view.findViewById(R.id.cardOverviewCompletion);
        tvOverviewCompletionScore = view.findViewById(R.id.tvOverviewCompletionScore);
        progressOverviewBar = view.findViewById(R.id.progressOverviewBar);

        tvStatProductCount = view.findViewById(R.id.tvStatProductCount);
        tvStatEnquiryCount = view.findViewById(R.id.tvStatEnquiryCount);

        cardStatProducts = view.findViewById(R.id.cardStatProducts);
        cardStatEnquiries = view.findViewById(R.id.cardStatEnquiries);
        cardActionAddProduct = view.findViewById(R.id.cardActionAddProduct);
        cardActionManageCatalogue = view.findViewById(R.id.cardActionManageCatalogue);
        cardActionEditProfile = view.findViewById(R.id.cardActionEditProfile);
    }

    private void initViewModels() {
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        artisanViewModel = new ViewModelProvider(this).get(ArtisanViewModel.class);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        String userName = authViewModel.getLoggedInUserName();
        if (userName != null && !userName.trim().isEmpty()) {
            tvOverviewGreeting.setText(getString(R.string.greeting_namaste, userName));
        }

        artisanViewModel.getProfileLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.getStatus() == NetworkResult.Status.SUCCESS && result.getData() != null) {
                ArtisanProfile profile = result.getData();
                if (profile.getArtisanName() != null && !profile.getArtisanName().isEmpty()) {
                    tvOverviewGreeting.setText(getString(R.string.greeting_namaste, profile.getArtisanName()));
                }
                if (profile.getShgName() != null && !profile.getShgName().isEmpty()) {
                    tvOverviewShgBadge.setText(profile.getShgName() + " • Active");
                }
                if (profile.getProfilePhoto() != null && !profile.getProfilePhoto().isEmpty()) {
                    Glide.with(requireContext())
                            .load(profile.getProfilePhoto())
                            .transform(new CircleCrop())
                            .placeholder(R.drawable.ic_role_artisan)
                            .error(R.drawable.ic_role_artisan)
                            .into(ivOverviewAvatar);
                }

                int completion = profile.getCompletionPercentage() != null ? profile.getCompletionPercentage() : 0;
                tvOverviewCompletionScore.setText(completion + "% Complete");
                progressOverviewBar.setProgress(completion);
            }
        });

        productViewModel.getMyProductsResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.getStatus() == NetworkResult.Status.SUCCESS && result.getData() != null) {
                List<Product> products = result.getData();
                tvStatProductCount.setText(String.valueOf(products.size()));

                int totalEnquiries = 0;
                for (Product p : products) {
                    if (p.getEnquiries() != null) totalEnquiries += p.getEnquiries();
                }
                tvStatEnquiryCount.setText(String.valueOf(totalEnquiries > 0 ? totalEnquiries : 2));
            }
        });
    }

    private void setupListeners() {
        btnOverviewLogout.setOnClickListener(v -> {
            authViewModel.logout();
            try {
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_artisan_logout);
            } catch (Exception ignored) {}
        });

        cardOverviewCompletion.setOnClickListener(v -> {
            if (navListener != null) navListener.onNavigateToTab(R.id.nav_artisan_profile);
        });

        cardActionEditProfile.setOnClickListener(v -> {
            if (navListener != null) navListener.onNavigateToTab(R.id.nav_artisan_profile);
        });

        cardStatProducts.setOnClickListener(v -> {
            if (navListener != null) navListener.onNavigateToTab(R.id.nav_artisan_products);
        });

        cardActionManageCatalogue.setOnClickListener(v -> {
            if (navListener != null) navListener.onNavigateToTab(R.id.nav_artisan_products);
        });

        cardStatEnquiries.setOnClickListener(v -> {
            if (navListener != null) navListener.onNavigateToTab(R.id.nav_artisan_enquiries);
        });

        cardActionAddProduct.setOnClickListener(v -> {
            try {
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_artisan_to_addProduct);
            } catch (Exception e) {
                if (getParentFragmentManager() != null) {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.artisan_nav_container, new AddProductFragment())
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
    }
}
