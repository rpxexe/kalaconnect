package com.kalaconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.kalaconnect.R;
import com.kalaconnect.adapters.OnboardingAdapter;
import com.kalaconnect.viewmodel.AuthViewModel;
import com.kalaconnect.viewmodel.OnboardingViewModel;

public class OnboardingFragment extends Fragment {

    private OnboardingViewModel onboardingViewModel;
    private AuthViewModel authViewModel;

    private ViewPager2 viewPager;
    private OnboardingAdapter adapter;
    private ImageView indicator0;
    private ImageView indicator1;
    private ImageView indicator2;
    private MaterialButton btnNext;
    private MaterialButton btnSkip;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        onboardingViewModel = new ViewModelProvider(this).get(OnboardingViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        viewPager = view.findViewById(R.id.viewPagerOnboarding);
        indicator0 = view.findViewById(R.id.indicator0);
        indicator1 = view.findViewById(R.id.indicator1);
        indicator2 = view.findViewById(R.id.indicator2);
        btnNext = view.findViewById(R.id.btnNext);
        btnSkip = view.findViewById(R.id.btnSkip);

        adapter = new OnboardingAdapter();
        viewPager.setAdapter(adapter);

        onboardingViewModel.getSlides().observe(getViewLifecycleOwner(), slides -> {
            adapter.setSlides(slides);
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateIndicators(position);

                int total = adapter.getItemCount();
                if (position == total - 1) {
                    btnNext.setText(R.string.onboarding_get_started);
                } else {
                    btnNext.setText(R.string.onboarding_next);
                }
            }
        });

        btnSkip.setOnClickListener(v -> completeOnboardingAndNavigate());

        btnNext.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current < adapter.getItemCount() - 1) {
                viewPager.setCurrentItem(current + 1, true);
            } else {
                completeOnboardingAndNavigate();
            }
        });
    }

    private void updateIndicators(int position) {
        indicator0.setImageResource(position == 0 ? R.drawable.bg_indicator_active : R.drawable.bg_indicator_inactive);
        indicator1.setImageResource(position == 1 ? R.drawable.bg_indicator_active : R.drawable.bg_indicator_inactive);
        indicator2.setImageResource(position == 2 ? R.drawable.bg_indicator_active : R.drawable.bg_indicator_inactive);
    }

    private void completeOnboardingAndNavigate() {
        authViewModel.setOnboardingCompleted();
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_onboarding_to_userTypeSelection);
    }
}
