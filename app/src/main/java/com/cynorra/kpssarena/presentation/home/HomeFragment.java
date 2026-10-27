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
import com.google.android.gms.ads.AdRequest;
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
        // The button consumes touches itself, so the card listener alone never fires from it
        binding.btnStartTest.setOnClickListener(v -> viewModel.startDailyTest());
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
        // Flashcards button
        binding.btnViewFlashcards.setOnClickListener(v -> {
            NavDirections action = HomeFragmentDirections.actionHomeToFlashcards();
            Navigation.findNavController(v).navigate(action);
        });
        // Mock exam card
        binding.cardMockExam.setOnClickListener(v -> {
            NavDirections action = HomeFragmentDirections.actionHomeToMockExam();
            Navigation.findNavController(v).navigate(action);
        });
        // Swipe refresh
        binding.swipeRefresh.setOnRefreshListener(() -> {
            viewModel.refreshData();
        });
        // AdMob banner
        binding.adBanner.loadAd(new AdRequest.Builder().build());
    }
    @Override
    public void onDestroyView() {
        binding.adBanner.destroy();
        super.onDestroyView();
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
                binding.tvTestStatus.setText(R.string.home_test_status_completed);
                binding.btnStartTest.setText(R.string.action_view_result);

            } else {
                binding.cardDailyTest.setAlpha(1.0f);
                binding.tvTestStatus.setText(R.string.home_test_status_ready);
                binding.btnStartTest.setText(R.string.action_start_test);
            }
        });
        // Mini leaderboard
        viewModel.getMiniLeaderboard().observe(getViewLifecycleOwner(), entries -> {
            if (entries != null) {
                miniLeaderboardAdapter.submitList(entries);
                binding.tvLeaderboardTitle.setText(R.string.home_leaderboard_title);
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
        // Navigation to today's saved result
        viewModel.getNavigateToResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                Bundle args = new Bundle();
                args.putSerializable("scoreResult", result);
                Navigation.findNavController(requireView()).navigate(R.id.testResultFragment, args);
                viewModel.onResultNavigated();
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
