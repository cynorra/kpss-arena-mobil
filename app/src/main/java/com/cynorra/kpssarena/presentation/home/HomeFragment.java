package com.cynorra.kpssarena.presentation.home;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentHomeBinding;
import com.cynorra.kpssarena.domain.model.DailyTest;
import com.cynorra.kpssarena.domain.model.LeaderboardEntry;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import com.cynorra.kpssarena.presentation.common.ShimmerUtils;

import com.cynorra.kpssarena.presentation.leaderboard.LeaderboardAdapter;
import java.util.List;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class HomeFragment extends BaseFragment<FragmentHomeBinding, HomeViewModel> {
    private LeaderboardAdapter miniLeaderboardAdapter;
    @Override
    protected FragmentHomeBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentHomeBinding.inflate(inflater, container, false);
    }
    @Override
    protected Class<HomeViewModel> getViewModelClass() {
        return HomeViewModel.class;
    }
    @Override
    protected void initViews() {
        // Mini leaderboard setup
        miniLeaderboardAdapter = new LeaderboardAdapter(true);
        binding.rvMiniLeaderboard.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMiniLeaderboard.setAdapter(miniLeaderboardAdapter);
        // Streak card click
        binding.cardStreak.setOnClickListener(v -> {
            NavDirections action = HomeFragmentDirections.actionHomeToStats();
            Navigation.findNavController(v).navigate(action);
        });
        // Test card click
        binding.cardDailyTest.setOnClickListener(v -> {
            viewModel.startDailyTest();
        });
        // Stats button

        binding.btnViewStats.setOnClickListener(v -> {
            NavDirections action = HomeFragmentDirections.actionHomeToStats();
            Navigation.findNavController(v).navigate(action);
        });
        // Mistakes button
        binding.btnViewMistakes.setOnClickListener(v -> {
            NavDirections action = HomeFragmentDirections.actionHomeToMistakes();
            Navigation.findNavController(v).navigate(action);
        });
        // Swipe refresh
        binding.swipeRefresh.setOnRefreshListener(() -> {
            viewModel.refreshData();
        });
    }
    @Override
    protected void observeData() {
        // User data
        viewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.tvUsername.setText(user.getUsername());
                binding.tvStreakCount.setText(String.valueOf(user.getStreak()));
                
                if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
                    com.bumptech.glide.Glide.with(this)
                            .load(user.getPhotoUrl())
                            .circleCrop()
                            .into(binding.ivAvatar);
                }
            }
        });
        // Daily test status
        viewModel.getTestCompleted().observe(getViewLifecycleOwner(), completed -> {
            if (completed != null && completed) {
                binding.cardDailyTest.setAlpha(0.6f);
                binding.tvTestStatus.setText("✅ Bugünkü test tamamlandı");
                binding.btnStartTest.setText("Sonucu Gör");

            } else {
                binding.cardDailyTest.setAlpha(1.0f);
                binding.tvTestStatus.setText("📝 Günlük test hazır");
                binding.btnStartTest.setText("Teste Başla");
            }
        });
        // Mini leaderboard
        viewModel.getMiniLeaderboard().observe(getViewLifecycleOwner(), entries -> {
            if (entries != null) {
                miniLeaderboardAdapter.submitList(entries);
                binding.tvLeaderboardTitle.setText("🏆 Bugünün Liderleri");
            }
        });
        // Navigation to test
        viewModel.getNavigateToTest().observe(getViewLifecycleOwner(), shouldNavigate -> {
            if (shouldNavigate != null && shouldNavigate) {
                NavDirections action = HomeFragmentDirections.actionHomeToTest();
                Navigation.findNavController(requireView()).navigate(action);
                viewModel.onNavigated();
            }
        });
        // Loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            binding.swipeRefresh.setRefreshing(loading != null && loading);
            if (loading != null && loading) {
                showShimmer(binding.shimmerTestCard, binding.shimmerLeaderboard);
            } else {
                hideShimmer(binding.shimmerTestCard, binding.shimmerLeaderboard);
            }
        });
        // Error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });

    }
}
