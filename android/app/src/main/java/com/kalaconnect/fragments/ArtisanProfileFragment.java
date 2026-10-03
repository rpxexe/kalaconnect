package com.kalaconnect.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kalaconnect.R;
import com.kalaconnect.models.ArtisanProfile;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.viewmodel.ArtisanViewModel;

import java.util.ArrayList;
import java.util.List;

public class ArtisanProfileFragment extends Fragment {

    private ArtisanViewModel artisanViewModel;

    private TextView tvCompletionPercentage;
    private LinearProgressIndicator progressCompletionBar;
    private TextView tvCompletionHint;
    private TextView tvProfileError;

    private FrameLayout layoutAvatarPicker;
    private ImageView ivArtisanPhoto;
    private TextView tvHeaderArtisanName;
    private TextView tvHeaderShgName;
    private TextView tvApprovalBadge;

    private TextInputLayout tilArtisanName;
    private TextInputEditText etArtisanName;
    private TextInputEditText etShgName;
    private TextInputEditText etArtisanBio;
    private TextInputEditText etVillageCity;
    private TextInputEditText etDistrict;
    private TextInputEditText etState;
    private TextInputEditText etExperience;

    private ChipGroup chipGroupContactPreference;
    private Chip chipContactPhone, chipContactWhatsapp, chipContactEmail;
    private ChipGroup chipGroupSkills;
    private MaterialButton btnSaveProfile;
    private FrameLayout loadingOverlayProfile;

    private String selectedProfilePhoto;
    private final List<String> selectedSkills = new ArrayList<>();
    private final List<String> availableSkillsList = new ArrayList<>(ArtisanViewModel.DEFAULT_SKILLS);

    private final ActivityResultLauncher<String> pickPhotoLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedProfilePhoto = uri.toString();
                    Glide.with(requireContext())
                            .load(uri)
                            .transform(new CircleCrop())
                            .into(ivArtisanPhoto);
                    updateDynamicCompletion();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artisan_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initViewModel();
        setupSkillsChips(availableSkillsList);
        setupListeners(view);

        artisanViewModel.loadProfile();
        artisanViewModel.loadSkills();
    }

    private void initViews(View view) {
        tvCompletionPercentage = view.findViewById(R.id.tvCompletionPercentage);
        progressCompletionBar = view.findViewById(R.id.progressCompletionBar);
        tvCompletionHint = view.findViewById(R.id.tvCompletionHint);
        tvProfileError = view.findViewById(R.id.tvProfileError);

        layoutAvatarPicker = view.findViewById(R.id.layoutAvatarPicker);
        ivArtisanPhoto = view.findViewById(R.id.ivArtisanPhoto);
        tvHeaderArtisanName = view.findViewById(R.id.tvHeaderArtisanName);
        tvHeaderShgName = view.findViewById(R.id.tvHeaderShgName);
        tvApprovalBadge = view.findViewById(R.id.tvApprovalBadge);

        tilArtisanName = view.findViewById(R.id.tilArtisanName);
        etArtisanName = view.findViewById(R.id.etArtisanName);
        etShgName = view.findViewById(R.id.etShgName);
        etArtisanBio = view.findViewById(R.id.etArtisanBio);
        etVillageCity = view.findViewById(R.id.etVillageCity);
        etDistrict = view.findViewById(R.id.etDistrict);
        etState = view.findViewById(R.id.etState);
        etExperience = view.findViewById(R.id.etExperience);

        chipGroupContactPreference = view.findViewById(R.id.chipGroupContactPreference);
        chipContactPhone = view.findViewById(R.id.chipContactPhone);
        chipContactWhatsapp = view.findViewById(R.id.chipContactWhatsapp);
        chipContactEmail = view.findViewById(R.id.chipContactEmail);

        chipGroupSkills = view.findViewById(R.id.chipGroupSkills);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);
        loadingOverlayProfile = view.findViewById(R.id.loadingOverlayProfile);
    }

    private void initViewModel() {
        artisanViewModel = new ViewModelProvider(this).get(ArtisanViewModel.class);

        artisanViewModel.getProfileLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.getStatus()) {
                case LOADING:
                    showLoading(true);
                    hideError();
                    break;
                case SUCCESS:
                    showLoading(false);
                    if (result.getData() != null) {
                        populateProfile(result.getData());
                    }
                    break;
                case ERROR:
                    showLoading(false);
                    showError(result.getMessage());
                    break;
            }
        });

        artisanViewModel.getSaveProfileResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            switch (result.getStatus()) {
                case LOADING:
                    showLoading(true);
                    hideError();
                    break;
                case SUCCESS:
                    showLoading(false);
                    Toast.makeText(requireContext(), R.string.artisan_profile_updated_toast, Toast.LENGTH_SHORT).show();
                    if (result.getData() != null) {
                        populateProfile(result.getData());
                    }
                    break;
                case ERROR:
                    showLoading(false);
                    showError(result.getMessage());
                    break;
            }
        });

        artisanViewModel.getSkillsLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.getStatus() == NetworkResult.Status.SUCCESS && result.getData() != null) {
                availableSkillsList.clear();
                availableSkillsList.addAll(result.getData());
                setupSkillsChips(availableSkillsList);
            }
        });
    }

    private void setupSkillsChips(List<String> skillsList) {
        chipGroupSkills.removeAllViews();
        for (String skill : skillsList) {
            Chip chip = new Chip(requireContext());
            chip.setText(skill);
            chip.setCheckable(true);
            chip.setChecked(selectedSkills.contains(skill));

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!selectedSkills.contains(skill)) selectedSkills.add(skill);
                } else {
                    selectedSkills.remove(skill);
                }
                updateDynamicCompletion();
            });

            chipGroupSkills.addView(chip);
        }
    }

    private void populateProfile(ArtisanProfile profile) {
        tvHeaderArtisanName.setText(profile.getArtisanName() != null ? profile.getArtisanName() : "Artisan");
        tvHeaderShgName.setText(profile.getShgName() != null && !profile.getShgName().isEmpty() ? profile.getShgName() : "Individual Artisan");

        etArtisanName.setText(profile.getArtisanName());
        etShgName.setText(profile.getShgName());
        etArtisanBio.setText(profile.getBio());
        etVillageCity.setText(profile.getVillageCity());
        etDistrict.setText(profile.getDistrict());
        etState.setText(profile.getState());
        etExperience.setText(profile.getExperience() != null && profile.getExperience() > 0 ? String.valueOf(profile.getExperience()) : "");

        selectedProfilePhoto = profile.getProfilePhoto();
        if (selectedProfilePhoto != null && !selectedProfilePhoto.isEmpty()) {
            Glide.with(requireContext())
                    .load(selectedProfilePhoto)
                    .transform(new CircleCrop())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(ivArtisanPhoto);
        }

        // Contact preference
        String contact = profile.getContactPreference() != null ? profile.getContactPreference() : "PHONE";
        if ("WHATSAPP".equalsIgnoreCase(contact)) {
            chipContactWhatsapp.setChecked(true);
        } else if ("EMAIL".equalsIgnoreCase(contact)) {
            chipContactEmail.setChecked(true);
        } else {
            chipContactPhone.setChecked(true);
        }

        // Verification status
        if (profile.isVerified()) {
            tvApprovalBadge.setText(R.string.badge_verified_artisan);
            tvApprovalBadge.setBackgroundResource(R.drawable.bg_status_badge_success);
            tvApprovalBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_success));
        } else {
            tvApprovalBadge.setText(R.string.badge_pending_verification);
            tvApprovalBadge.setBackgroundResource(R.drawable.bg_status_badge_pending);
            tvApprovalBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_pending));
        }

        // Skills
        selectedSkills.clear();
        if (profile.getSkills() != null) {
            selectedSkills.addAll(profile.getSkills());
        }
        setupSkillsChips(availableSkillsList);

        // Completion percentage
        updateDynamicCompletion();
    }

    private void setupListeners(View view) {
        layoutAvatarPicker.setOnClickListener(v -> {
            try {
                pickPhotoLauncher.launch("image/*");
            } catch (Exception e) {
                // Friendly fallback for demo
                selectedProfilePhoto = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&auto=format&fit=crop";
                Glide.with(requireContext()).load(selectedProfilePhoto).transform(new CircleCrop()).into(ivArtisanPhoto);
                updateDynamicCompletion();
                Toast.makeText(requireContext(), R.string.profile_photo_updated, Toast.LENGTH_SHORT).show();
            }
        });

        TextWatcher completionWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                updateDynamicCompletion();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        etArtisanName.addTextChangedListener(completionWatcher);
        etShgName.addTextChangedListener(completionWatcher);
        etArtisanBio.addTextChangedListener(completionWatcher);
        etVillageCity.addTextChangedListener(completionWatcher);
        etDistrict.addTextChangedListener(completionWatcher);
        etState.addTextChangedListener(completionWatcher);
        etExperience.addTextChangedListener(completionWatcher);

        chipGroupContactPreference.setOnCheckedStateChangeListener((group, checkedIds) -> updateDynamicCompletion());

        TextView tvLanguage = view.findViewById(R.id.tvArtisanCurrentLanguage);
        View cardLanguage = view.findViewById(R.id.cardArtisanLanguageSettings);
        if (tvLanguage != null) {
            String lang = com.kalaconnect.utils.LocaleHelper.getLanguage(requireContext());
            if (com.kalaconnect.utils.LocaleHelper.LANGUAGE_HINDI.equals(lang)) {
                tvLanguage.setText(R.string.language_hindi);
            } else if (com.kalaconnect.utils.LocaleHelper.LANGUAGE_MARATHI.equals(lang)) {
                tvLanguage.setText(R.string.language_marathi);
            } else {
                tvLanguage.setText(R.string.language_english);
            }
        }
        if (cardLanguage != null) {
            cardLanguage.setOnClickListener(v -> {
                LanguageSelectionDialogFragment.newInstance().show(getChildFragmentManager(), LanguageSelectionDialogFragment.TAG);
            });
        }

        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void updateDynamicCompletion() {
        String name = etArtisanName.getText() != null ? etArtisanName.getText().toString().trim() : "";
        String shg = etShgName.getText() != null ? etShgName.getText().toString().trim() : "";
        String bio = etArtisanBio.getText() != null ? etArtisanBio.getText().toString().trim() : "";
        String village = etVillageCity.getText() != null ? etVillageCity.getText().toString().trim() : "";
        String district = etDistrict.getText() != null ? etDistrict.getText().toString().trim() : "";
        String state = etState.getText() != null ? etState.getText().toString().trim() : "";
        String expStr = etExperience.getText() != null ? etExperience.getText().toString().trim() : "";
        int exp = 0;
        try {
            if (!TextUtils.isEmpty(expStr)) exp = Integer.parseInt(expStr);
        } catch (Exception ignored) {}

        String contactPref = getSelectedContactPreference();

        int percentage = artisanViewModel.calculateCompletion(
                name, shg, bio, selectedProfilePhoto, village, district, state, exp, selectedSkills, contactPref
        );

        tvCompletionPercentage.setText(percentage + "%");
        progressCompletionBar.setProgress(percentage);

        if (percentage == 100) {
            tvCompletionHint.setText(R.string.profile_complete_ready);
        } else {
            tvCompletionHint.setText(R.string.profile_incomplete_hint);
        }
    }

    private String getSelectedContactPreference() {
        if (chipContactWhatsapp.isChecked()) return "WHATSAPP";
        if (chipContactEmail.isChecked()) return "EMAIL";
        return "PHONE";
    }

    private void saveProfile() {
        String name = etArtisanName.getText() != null ? etArtisanName.getText().toString().trim() : "";
        String shg = etShgName.getText() != null ? etShgName.getText().toString().trim() : "";
        String bio = etArtisanBio.getText() != null ? etArtisanBio.getText().toString().trim() : "";
        String village = etVillageCity.getText() != null ? etVillageCity.getText().toString().trim() : "";
        String district = etDistrict.getText() != null ? etDistrict.getText().toString().trim() : "";
        String state = etState.getText() != null ? etState.getText().toString().trim() : "";
        String expStr = etExperience.getText() != null ? etExperience.getText().toString().trim() : "";

        tilArtisanName.setError(null);
        hideError();

        if (TextUtils.isEmpty(name)) {
            tilArtisanName.setError("Full name is required");
            return;
        }

        int exp = 0;
        try {
            if (!TextUtils.isEmpty(expStr)) exp = Integer.parseInt(expStr);
        } catch (Exception ignored) {}

        ArtisanProfile profile = new ArtisanProfile();
        profile.setArtisanName(name);
        profile.setShgName(shg);
        profile.setBio(bio);
        profile.setVillageCity(village);
        profile.setDistrict(district);
        profile.setState(state);
        profile.setExperience(exp);
        profile.setContactPreference(getSelectedContactPreference());
        profile.setProfilePhoto(selectedProfilePhoto);
        profile.setSkills(new ArrayList<>(selectedSkills));

        artisanViewModel.saveProfile(profile);
    }

    private void showLoading(boolean isLoading) {
        if (loadingOverlayProfile != null) {
            loadingOverlayProfile.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        if (btnSaveProfile != null) {
            btnSaveProfile.setEnabled(!isLoading);
        }
    }

    private void showError(String message) {
        if (tvProfileError != null) {
            tvProfileError.setText(message != null ? message : "Unable to save profile");
            tvProfileError.setVisibility(View.VISIBLE);
        }
    }

    private void hideError() {
        if (tvProfileError != null) {
            tvProfileError.setVisibility(View.GONE);
        }
    }
}
