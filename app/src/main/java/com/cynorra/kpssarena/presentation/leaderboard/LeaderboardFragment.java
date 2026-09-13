package com.cynorra.kpssarena.presentation.leaderboard;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentLeaderboardBinding;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import com.google.android.material.tabs.TabLayout;

import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class LeaderboardFragment extends BaseFragment<FragmentLeaderboardBinding, LeaderboardViewModel> {
    private LeaderboardAdapter adapter;
    @Override
    protected FragmentLeaderboardBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentLeaderboardBinding.inflate(inflater, container, false);
    }
    @Override
    protected Class<LeaderboardViewModel> getViewModelClass() {
        return LeaderboardViewModel.class;
    }
    @Override
    protected void initViews() {
        adapter = new LeaderboardAdapter(false);
        binding.rvLeaderboard.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvLeaderboard.setAdapter(adapter);
        // Tab setup
        binding.tabFilter.addTab(binding.tabFilter.newTab().setText(R.string.leaderboard_tab_today));
        binding.tabFilter.addTab(binding.tabFilter.newTab().setText(R.string.leaderboard_tab_weekly));
        binding.tabFilter.addTab(binding.tabFilter.newTab().setText(R.string.leaderboard_tab_monthly));
        binding.tabFilter.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewModel.setFilter(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}

        });
        // Swipe refresh
        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.refreshLeaderboard());
        // Item click
        adapter.setOnItemClickListener(entry -> {
            if (entry.getUid() != null) {
                NavDirections action = LeaderboardFragmentDirections
                    .actionLeaderboardToUserProfile();
                Bundle args = new Bundle();
                args.putString("userId", entry.getUid());
                Navigation.findNavController(requireView()).navigate(
                    R.id.userProfileFragment, args);
            }
        });
    }
    @Override
    protected void observeData() {
        viewModel.getLeaderboardEntries().observe(getViewLifecycleOwner(), entries -> {
            if (entries != null) {
                adapter.submitList(entries);
                binding.emptyStateLeaderboard.setVisibility(entries.isEmpty() ? View.VISIBLE : View.GONE);
                binding.swipeRefresh.setVisibility(entries.isEmpty() ? View.GONE : View.VISIBLE);
            }
        });
        viewModel.getCurrentUserEntry().observe(getViewLifecycleOwner(), entry -> {
            if (entry != null) {
                binding.tvCurrentUserRank.setText(entry.getRankBadge());
                binding.tvCurrentUserScore.setText(String.valueOf(entry.getTotalScore()));
            }
        });
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            binding.swipeRefresh.setRefreshing(loading != null && loading);
        });
    }
}
