package com.cynorra.kpssarena.presentation.onboarding;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentOnboardingBinding;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import com.cynorra.kpssarena.presentation.splash.SplashViewModel;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class OnboardingFragment extends BaseFragment<FragmentOnboardingBinding, SplashViewModel> {
    private OnboardingAdapter adapter;
    @Override
    protected FragmentOnboardingBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentOnboardingBinding.inflate(inflater, container, false);
    }

    @Override
    protected Class<SplashViewModel> getViewModelClass() {
        return SplashViewModel.class;
    }
    @Override
    protected void initViews() {
        adapter = new OnboardingAdapter(requireContext());
        binding.viewPager.setAdapter(adapter);
        
        binding.dotsIndicator.setViewPager2(binding.viewPager);
        binding.btnNext.setOnClickListener(v -> {
            int current = binding.viewPager.getCurrentItem();
            if (current < adapter.getItemCount() - 1) {
                binding.viewPager.setCurrentItem(current + 1);
            }
        });
        binding.btnSkip.setOnClickListener(v -> navigateToLogin());
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == adapter.getItemCount() - 1) {
                    binding.btnNext.setText(R.string.action_onboarding_start);
                    binding.btnNext.setOnClickListener(v -> navigateToLogin());
                } else {
                    binding.btnNext.setText(R.string.action_forward);
                    binding.btnNext.setOnClickListener(v -> binding.viewPager.setCurrentItem(position + 1));
                }
            }
        });
    }
    private void navigateToLogin() {
        viewModel.setOnboardingCompleted();
        NavDirections action = OnboardingFragmentDirections.actionOnboardingToLogin();
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    protected void observeData() {
        // No ViewModel observations needed
    }
}
