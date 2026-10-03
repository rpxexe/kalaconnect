package com.kalaconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kalaconnect.R;
import com.kalaconnect.adapters.NotificationAdapter;
import com.kalaconnect.models.NotificationItem;
import com.kalaconnect.repository.NotificationRepository;

import java.util.ArrayList;
import java.util.List;

public class ArtisanNotificationsFragment extends Fragment {

    private RecyclerView rvArtisanNotifications;
    private LinearLayout layoutNotificationsEmpty;
    private NotificationAdapter notificationAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artisan_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvArtisanNotifications = view.findViewById(R.id.rvArtisanNotifications);
        layoutNotificationsEmpty = view.findViewById(R.id.layoutNotificationsEmpty);

        notificationAdapter = new NotificationAdapter();
        rvArtisanNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvArtisanNotifications.setAdapter(notificationAdapter);

        loadNotifications();
    }

    private void loadNotifications() {
        List<NotificationItem> items = new ArrayList<>();
        items.add(new NotificationItem(1L, "New Customer Enquiry", "Ananya Sharma sent an inquiry about Terracotta Decorative Vase", "ENQUIRY", false, "15m ago"));
        items.add(new NotificationItem(2L, "Product Published", "Your handcrafted item is now visible on the public marketplace", "PRODUCT", true, "2h ago"));
        items.add(new NotificationItem(3L, "Profile Verified", "NGO Admin verified your SHG association credentials", "VERIFICATION", true, "1d ago"));

        notificationAdapter.setNotifications(items);

        if (items.isEmpty()) {
            layoutNotificationsEmpty.setVisibility(View.VISIBLE);
            rvArtisanNotifications.setVisibility(View.GONE);
        } else {
            layoutNotificationsEmpty.setVisibility(View.GONE);
            rvArtisanNotifications.setVisibility(View.VISIBLE);
        }

        NotificationRepository notificationRepo = new NotificationRepository(requireContext());
        notificationRepo.getNotifications(false).observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.getStatus() == com.kalaconnect.network.NetworkResult.Status.SUCCESS && result.getData() != null && !result.getData().isEmpty()) {
                notificationAdapter.setNotifications(result.getData());
                layoutNotificationsEmpty.setVisibility(View.GONE);
                rvArtisanNotifications.setVisibility(View.VISIBLE);
            }
        });
    }
}
