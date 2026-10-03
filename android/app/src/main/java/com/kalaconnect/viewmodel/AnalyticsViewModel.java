package com.kalaconnect.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.kalaconnect.models.AnalyticsData;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.repository.AnalyticsRepository;

public class AnalyticsViewModel extends AndroidViewModel {

    private final AnalyticsRepository analyticsRepository;
    private final MediatorLiveData<NetworkResult<AnalyticsData>> analyticsLiveData = new MediatorLiveData<>();

    public AnalyticsViewModel(@NonNull Application application) {
        super(application);
        this.analyticsRepository = new AnalyticsRepository(application.getApplicationContext());
    }

    public LiveData<NetworkResult<AnalyticsData>> getAnalyticsLiveData() {
        return analyticsLiveData;
    }

    public void loadAnalytics() {
        analyticsLiveData.addSource(analyticsRepository.getAnalytics(), result -> {
            analyticsLiveData.setValue(result);
        });
    }
}
