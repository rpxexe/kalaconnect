package com.kalaconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kalaconnect.R;
import com.kalaconnect.adapters.EnquiryAdapter;
import com.kalaconnect.models.EnquiryItem;
import com.kalaconnect.network.NetworkResult;
import com.kalaconnect.viewmodel.CustomerViewModel;

import java.util.List;

public class CustomerEnquiriesFragment extends Fragment {

    private CustomerViewModel customerViewModel;
    private RecyclerView rvEnquiries;
    private ProgressBar pbLoading;
    private View layoutEmpty;
    private EnquiryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_enquiries, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        customerViewModel = new ViewModelProvider(requireActivity()).get(CustomerViewModel.class);

        rvEnquiries = view.findViewById(R.id.rvCustomerEnquiries);
        pbLoading = view.findViewById(R.id.pbCustomerEnquiries);
        layoutEmpty = view.findViewById(R.id.layoutCustomerEnquiriesEmpty);

        adapter = new EnquiryAdapter();
        rvEnquiries.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvEnquiries.setAdapter(adapter);

        adapter.setOnEnquiryClickListener(item -> {
            Toast.makeText(requireContext(), item.getProductName(), Toast.LENGTH_SHORT).show();
        });

        loadEnquiries();
    }

    private void loadEnquiries() {
        pbLoading.setVisibility(View.VISIBLE);
        customerViewModel.getMyEnquiries().observe(getViewLifecycleOwner(), result -> {
            pbLoading.setVisibility(View.GONE);
            if (result != null && result.getStatus() == NetworkResult.Status.SUCCESS && result.getData() != null) {
                List<EnquiryItem> list = result.getData();
                adapter.setEnquiries(list);
                if (list.isEmpty()) {
                    layoutEmpty.setVisibility(View.VISIBLE);
                    rvEnquiries.setVisibility(View.GONE);
                } else {
                    layoutEmpty.setVisibility(View.GONE);
                    rvEnquiries.setVisibility(View.VISIBLE);
                }
            } else {
                layoutEmpty.setVisibility(View.VISIBLE);
                rvEnquiries.setVisibility(View.GONE);
            }
        });
    }
}
