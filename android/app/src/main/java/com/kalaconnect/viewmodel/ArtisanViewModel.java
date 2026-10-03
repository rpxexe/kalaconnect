package com.kalaconnect.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.kalaconnect.models.ArtisanProfile;
import com.kalaconnect.models.EnquiryItem;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.repository.ArtisanRepository;
import com.kalaconnect.repository.EnquiryRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArtisanViewModel extends AndroidViewModel {

    private final ArtisanRepository artisanRepository;
    private final EnquiryRepository enquiryRepository;

    private final MediatorLiveData<NetworkResult<ArtisanProfile>> profileLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<NetworkResult<ArtisanProfile>> saveProfileResult = new MediatorLiveData<>();
    private final MediatorLiveData<NetworkResult<List<String>>> skillsLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<NetworkResult<List<EnquiryItem>>> enquiriesLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<NetworkResult<EnquiryItem>> updateStatusResult = new MediatorLiveData<>();

    // Default core skills list
    public static final List<String> DEFAULT_SKILLS = Arrays.asList(
            "Pottery",
            "Embroidery",
            "Weaving",
            "Painting",
            "Jewellery",
            "Woodcraft",
            "Bamboo craft",
            "Textile craft"
    );

    public ArtisanViewModel(@NonNull Application application) {
        super(application);
        this.artisanRepository = new ArtisanRepository(application);
        this.enquiryRepository = new EnquiryRepository(application);
    }

    public LiveData<NetworkResult<ArtisanProfile>> getProfileLiveData() {
        return profileLiveData;
    }

    public LiveData<NetworkResult<ArtisanProfile>> getSaveProfileResult() {
        return saveProfileResult;
    }

    public LiveData<NetworkResult<List<String>>> getSkillsLiveData() {
        return skillsLiveData;
    }

    public LiveData<NetworkResult<List<EnquiryItem>>> getEnquiriesLiveData() {
        return enquiriesLiveData;
    }

    public LiveData<NetworkResult<EnquiryItem>> getUpdateStatusResult() {
        return updateStatusResult;
    }

    public void loadEnquiries(String status) {
        LiveData<NetworkResult<List<EnquiryItem>>> source = enquiryRepository.getEnquiries(status);
        enquiriesLiveData.addSource(source, result -> {
            enquiriesLiveData.setValue(result);
            if (result.getStatus() != NetworkResult.Status.LOADING) {
                enquiriesLiveData.removeSource(source);
            }
        });
    }

    public void updateEnquiryStatus(Long id, String status) {
        LiveData<NetworkResult<EnquiryItem>> source = enquiryRepository.updateEnquiryStatus(id, status);
        updateStatusResult.addSource(source, result -> {
            updateStatusResult.setValue(result);
            if (result.getStatus() != NetworkResult.Status.LOADING) {
                updateStatusResult.removeSource(source);
                if (result.getStatus() == NetworkResult.Status.SUCCESS) {
                    loadEnquiries(null); // reload list
                }
            }
        });
    }

    public void loadProfile() {
        LiveData<NetworkResult<ArtisanProfile>> source = artisanRepository.getProfile();
        profileLiveData.addSource(source, result -> {
            profileLiveData.setValue(result);
            if (result.getStatus() != NetworkResult.Status.LOADING) {
                profileLiveData.removeSource(source);
            }
        });
    }

    public void saveProfile(ArtisanProfile profile) {
        LiveData<NetworkResult<ArtisanProfile>> source = artisanRepository.updateProfile(profile);
        saveProfileResult.addSource(source, result -> {
            saveProfileResult.setValue(result);
            if (result.getStatus() == NetworkResult.Status.SUCCESS) {
                profileLiveData.setValue(result);
            }
            if (result.getStatus() != NetworkResult.Status.LOADING) {
                saveProfileResult.removeSource(source);
            }
        });
    }

    public void loadSkills() {
        LiveData<NetworkResult<List<String>>> source = artisanRepository.getAvailableSkills();
        skillsLiveData.addSource(source, result -> {
            if (result.getStatus() == NetworkResult.Status.ERROR) {
                // If network fails, supply the default standard list
                skillsLiveData.setValue(NetworkResult.success(DEFAULT_SKILLS));
            } else {
                skillsLiveData.setValue(result);
            }
            if (result.getStatus() != NetworkResult.Status.LOADING) {
                skillsLiveData.removeSource(source);
            }
        });
    }

    public int calculateCompletion(String name, String shg, String bio, String photo,
                                  String village, String district, String state,
                                  int experience, List<String> skills, String contactPref) {
        int score = 0;
        if (name != null && !name.trim().isEmpty()) score += 10;
        if (shg != null && !shg.trim().isEmpty()) score += 10;
        if (bio != null && !bio.trim().isEmpty()) score += 10;
        if (photo != null && !photo.trim().isEmpty()) score += 10;
        if (village != null && !village.trim().isEmpty()) score += 10;
        if (district != null && !district.trim().isEmpty()) score += 10;
        if (state != null && !state.trim().isEmpty()) score += 10;
        if (experience > 0) score += 10;
        if (skills != null && !skills.isEmpty()) score += 10;
        if (contactPref != null && !contactPref.trim().isEmpty()) score += 10;
        return Math.min(score, 100);
    }
}
