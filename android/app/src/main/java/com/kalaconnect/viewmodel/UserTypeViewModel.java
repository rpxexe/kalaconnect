package com.kalaconnect.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kalaconnect.models.UserRole;

public class UserTypeViewModel extends ViewModel {

    private final MutableLiveData<UserRole> selectedRoleLiveData = new MutableLiveData<>(UserRole.ARTISAN);

    public LiveData<UserRole> getSelectedRole() {
        return selectedRoleLiveData;
    }

    public void selectRole(UserRole role) {
        selectedRoleLiveData.setValue(role);
    }
}
