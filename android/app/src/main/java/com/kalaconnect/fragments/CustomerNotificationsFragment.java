package com.kalaconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kalaconnect.R;
import com.kalaconnect.adapters.NotificationAdapter;
import com.kalaconnect.models.NotificationItem;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.repository.NotificationRepository;

import java.util.ArrayList;
import java.util.List;

public class CustomerNotificationsFragment extends Fragment {

    private RecyclerView rvNotifications;
    private View layoutEmpty;
    private NotificationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvNotifications = view.findViewById(R.id.rvCustomerNotifications);
        layoutEmpty = view.findViewById(R.id.layoutCustomerNotificationsEmpty);

        adapter = new NotificationAdapter();
        rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvNotifications.setAdapter(adapter);

        loadNotifications();
    }

    private void loadNotifications() {
        List<NotificationItem> items = new ArrayList<>();
        items.add(new NotificationItem(1L, "Artisan Response", "Gorakhpur Clay Masters replied to your enquiry regarding Terracotta Chai Vessels.", "ENQUIRY", false, "20m ago"));
        items.add(new NotificationItem(2L, "New Collection Released", "Lakshmi Mahila Collective published 4 new Madhubani hand-painted canvases.", "PRODUCT", false, "3h ago"));
        items.add(new NotificationItem(3L, "Patronage Welcome", "Thank you for supporting traditional Indian self-help group artisans.", "SYSTEM", true, "2d ago"));

        adapter.setNotifications(items);
        layoutEmpty.setVisibility(View.GONE);
        rvNotifications.setVisibility(View.VISIBLE);

        NotificationRepository notificationRepo = new NotificationRepository(requireContext());
        notificationRepo.getNotifications(false).observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.getStatus() == NetworkResult.Status.SUCCESS && result.getData() != null && !result.getData().isEmpty()) {
                adapter.setNotifications(result.getData());
                layoutEmpty.setVisibility(View.GONE);
                rvNotifications.setVisibility(View.VISIBLE);
            }
        });
    }
}
