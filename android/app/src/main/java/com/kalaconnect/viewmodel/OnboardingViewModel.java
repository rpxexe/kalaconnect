package com.kalaconnect.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kalaconnect.R;
import com.kalaconnect.models.OnboardingSlide;

import java.util.ArrayList;
import java.util.List;

public class OnboardingViewModel extends ViewModel {

    private final MutableLiveData<List<OnboardingSlide>> slidesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentPositionLiveData = new MutableLiveData<>(0);

    public OnboardingViewModel() {
        loadSlides();
    }

    private void loadSlides() {
        List<OnboardingSlide> slides = new ArrayList<>();
        slides.add(new OnboardingSlide(
                "Empower Rural Artisans",
                "Connect directly with Self-Help Groups and individual craftspeople to showcase authentic Indian heritage crafts.",
                R.drawable.ic_kala_logo
        ));
        slides.add(new OnboardingSlide(
                "AI-Powered Catalogs",
                "Generate captivating promotional stories, cultural provenance descriptions, and market-ready catalogs effortlessly.",
                R.drawable.ic_craft
        ));
        slides.add(new OnboardingSlide(
                "Transparent Community",
                "Collaborate with verified NGOs, eliminate exploitative middlemen, and receive direct customer enquiries.",
                R.drawable.ic_role_ngo
        ));
        slidesLiveData.setValue(slides);
    }

    public LiveData<List<OnboardingSlide>> getSlides() {
        return slidesLiveData;
    }

    public LiveData<Integer> getCurrentPosition() {
        return currentPositionLiveData;
    }

    public void setCurrentPosition(int position) {
        currentPositionLiveData.setValue(position);
    }
}
