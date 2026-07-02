package com.cynorra.kpssarena.presentation.premium;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentPremiumBinding;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class PremiumFragment extends BaseFragment<FragmentPremiumBinding, Prem
    @Override
    protected FragmentPremiumBinding inflateBinding(LayoutInflater inflater, ViewGroup
        return FragmentPremiumBinding.inflate(inflater, container, false);
    }
    @Override
    protected Class<PremiumViewModel> getViewModelClass() {
        return PremiumViewModel.class;
    }
    @Override
    protected void initViews() {
        binding.btnMonthly.setOnClickListener(v -> viewModel.purchaseMonthly());
        binding.btnYearly.setOnClickListener(v -> viewModel.purchaseYearly());
        binding.btnRestorePurchases.setOnClickListener(v -> viewModel.restorePurchases(
    }
    @Override
    protected void observeData() {
        viewModel.getIsPremium().observe(getViewLifecycleOwner(), isPremium -> {
            if (isPremium != null && isPremium) {
                Toast.makeText(getContext(), "Premium üyeliğiniz aktif! 🎉", Toast.LENGTH_LON
                Navigation.findNavController(requireView()).popBackStack();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
