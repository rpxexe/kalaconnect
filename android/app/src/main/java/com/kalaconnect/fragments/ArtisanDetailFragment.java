package com.kalaconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.kalaconnect.R;
import com.kalaconnect.adapters.ProductAdapter;
import com.kalaconnect.models.Artisan;
import com.kalaconnect.models.ArtisanProfile;
import com.kalaconnect.models.Product;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.viewmodel.CustomerViewModel;

import java.util.ArrayList;
import java.util.List;

public class ArtisanDetailFragment extends Fragment {

    private CustomerViewModel customerViewModel;
    private Artisan artisan;
    private ArtisanProfile artisanProfile;

    private MaterialToolbar toolbar;
    private ImageView ivPhoto;
    private TextView tvName;
    private ImageView ivVerified;
    private TextView tvShg;
    private TextView tvLocation;
    private TextView tvExperience;
    private ChipGroup chipGroupSkills;
    private TextView tvBio;
    private RecyclerView rvProducts;
    private TextView tvNoProducts;

    private ProductAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artisan_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        customerViewModel = new ViewModelProvider(requireActivity()).get(CustomerViewModel.class);

        initViews(view);

        long artisanId = -1L;
        if (getArguments() != null) {
            artisan = (Artisan) getArguments().getSerializable("artisan");
            artisanProfile = (ArtisanProfile) getArguments().getSerializable("artisanProfile");
            artisanId = getArguments().getLong("artisanId", -1L);

            if (artisan != null) {
                bindArtisan(artisan);
                artisanId = artisan.getId() != null ? artisan.getId() : artisanId;
            } else if (artisanProfile != null) {
                bindArtisanProfile(artisanProfile);
                artisanId = artisanProfile.getId() != null ? artisanProfile.getId() : artisanId;
            }
        }

        if (artisanId > 0) {
            fetchArtisanDetail(artisanId);
        }
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbarArtisanDetail);
        ivPhoto = view.findViewById(R.id.ivArtisanDetailPhoto);
        tvName = view.findViewById(R.id.tvArtisanDetailName);
        ivVerified = view.findViewById(R.id.ivArtisanDetailVerified);
        tvShg = view.findViewById(R.id.tvArtisanDetailShg);
        tvLocation = view.findViewById(R.id.tvArtisanDetailLocation);
        tvExperience = view.findViewById(R.id.tvArtisanDetailExperience);
        chipGroupSkills = view.findViewById(R.id.chipGroupArtisanSkills);
        tvBio = view.findViewById(R.id.tvArtisanDetailBio);
        rvProducts = view.findViewById(R.id.rvArtisanDetailProducts);
        tvNoProducts = view.findViewById(R.id.tvArtisanDetailNoProducts);

        toolbar.setNavigationOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigateUp();
        });

        productAdapter = new ProductAdapter();
        rvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvProducts.setAdapter(productAdapter);

        productAdapter.setOnProductClickListener(this::openProductDetail);
    }

    private void fetchArtisanDetail(long id) {
        customerViewModel.getArtisanDetail(id).observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.getStatus() == NetworkResult.Status.SUCCESS && result.getData() != null) {
                this.artisanProfile = result.getData();
                bindArtisanProfile(this.artisanProfile);
            }
        });
    }

    private void bindArtisan(@NonNull Artisan a) {
        tvName.setText(a.getName());
        tvShg.setText(R.string.artisan_collective_default);
        tvLocation.setText(a.getLocation() != null ? a.getLocation() : "India");
        tvExperience.setText(R.string.master_craftsman);
        ivVerified.setVisibility(a.isVerified() ? View.VISIBLE : View.GONE);

        if (a.getBio() != null) {
            tvBio.setText(a.getBio());
        }

        chipGroupSkills.removeAllViews();
        if (a.getCraftSpecialty() != null && !a.getCraftSpecialty().isEmpty()) {
            String[] parts = a.getCraftSpecialty().split("•");
            for (String part : parts) {
                addSkillChip(part.trim());
            }
        }

        if (a.getAvatarUrl() != null && !a.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(a.getAvatarUrl())
                    .transform(new CircleCrop())
                    .placeholder(R.drawable.ic_role_artisan)
                    .into(ivPhoto);
        } else {
            Glide.with(this)
                    .load(a.getAvatarResId() != 0 ? a.getAvatarResId() : R.drawable.ic_role_artisan)
                    .transform(new CircleCrop())
                    .into(ivPhoto);
        }
    }

    private void bindArtisanProfile(@NonNull ArtisanProfile ap) {
        String name = ap.getArtisanName();
        if (name == null || name.isBlank()) {
            name = ap.getShgName() != null ? ap.getShgName() : getString(R.string.master_craftsman);
        }
        tvName.setText(name);

        if (ap.getShgName() != null && !ap.getShgName().isBlank()) {
            tvShg.setText(getString(R.string.shg_prefix, ap.getShgName()));
            tvShg.setVisibility(View.VISIBLE);
        } else {
            tvShg.setVisibility(View.GONE);
        }

        StringBuilder loc = new StringBuilder();
        if (ap.getVillageCity() != null) loc.append(ap.getVillageCity());
        if (ap.getDistrict() != null) {
            if (loc.length() > 0) loc.append(", ");
            loc.append(ap.getDistrict());
        }
        if (ap.getState() != null) {
            if (loc.length() > 0) loc.append(", ");
            loc.append(ap.getState());
        }
        tvLocation.setText(loc.length() > 0 ? loc.toString() : "India");

        int exp = ap.getExperience() != null ? ap.getExperience() : 0;
        tvExperience.setText(exp > 0 ? exp + " Years Traditional Mastery" : "Verified Artisan Collective");
        ivVerified.setVisibility(ap.isVerified() ? View.VISIBLE : View.GONE);

        if (ap.getBio() != null && !ap.getBio().isBlank()) {
            tvBio.setText(ap.getBio());
        }

        chipGroupSkills.removeAllViews();
        if (ap.getSkills() != null && !ap.getSkills().isEmpty()) {
            for (String skill : ap.getSkills()) {
                addSkillChip(skill);
            }
        }

        if (ap.getProfilePhoto() != null && !ap.getProfilePhoto().isEmpty()) {
            Glide.with(this)
                    .load(ap.getProfilePhoto())
                    .transform(new CircleCrop())
                    .placeholder(R.drawable.ic_role_artisan)
                    .into(ivPhoto);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_role_artisan)
                    .transform(new CircleCrop())
                    .into(ivPhoto);
        }

        List<Product> prods = ap.getProducts();
        if (prods != null && !prods.isEmpty()) {
            productAdapter.setProducts(prods);
            rvProducts.setVisibility(View.VISIBLE);
            tvNoProducts.setVisibility(View.GONE);
        } else {
            rvProducts.setVisibility(View.GONE);
            tvNoProducts.setVisibility(View.VISIBLE);
        }
    }

    private void addSkillChip(String skillName) {
        Chip chip = new Chip(requireContext());
        chip.setText(skillName);
        chip.setClickable(false);
        chipGroupSkills.addView(chip);
    }

    private void openProductDetail(Product product) {
        Bundle args = new Bundle();
        args.putSerializable("product", product);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.productDetailFragment, args);
    }
}
