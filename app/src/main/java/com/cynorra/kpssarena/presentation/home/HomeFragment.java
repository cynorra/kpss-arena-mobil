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
import com.cynorra.kpssarena.presentation.common.AnimUtils;
import com.cynorra.kpssarena.presentation.common.ShimmerUtils;

import com.cynorra.kpssarena.presentation.leaderboard.LeaderboardAdapter;
import java.util.List;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class HomeFragment extends BaseFragment<FragmentHomeBinding, HomeViewModel> {
    private LeaderboardAdapter miniLeaderboardAdapter;
    private boolean reminderOfferShown = false;
    private final com.cynorra.kpssarena.notification.ReminderPermissionRequester reminderPermission =
            new com.cynorra.kpssarena.notification.ReminderPermissionRequester(this, allowed -> {
                if (!isAdded()) return;
                com.cynorra.kpssarena.notification.ReminderScheduler.setEnabled(requireContext(), allowed);
            });
    /** Ask once, right after the first successful home load, instead of only after the first test result. */
    private void offerRemindersOnce() {
        if (reminderOfferShown || !isAdded()) return;
        android.content.Context ctx = requireContext();
        if (com.cynorra.kpssarena.notification.ReminderScheduler.wasPrompted(ctx)
                || com.cynorra.kpssarena.notification.ReminderScheduler.isEnabled(ctx)) {
            return;
        }
        reminderOfferShown = true;
        com.cynorra.kpssarena.notification.ReminderScheduler.markPrompted(ctx);
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(ctx)
                .setTitle(R.string.notif_offer_title)
                .setMessage(R.string.notif_offer_message)
                .setPositiveButton(R.string.notif_offer_yes, (d, w) -> reminderPermission.request())
                .setNegativeButton(R.string.notif_offer_no, null)
                .show();
    }
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
        // Practice card
        binding.cardPractice.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(HomeFragmentDirections.actionHomeToPractice()));
        // Invite card
        binding.cardInvite.setOnClickListener(v -> shareInvite());
        // KPSS Exam Hub / Deneme Sınavları card
        binding.cardMockExam.setOnClickListener(v -> {
            com.cynorra.kpssarena.presentation.mockexam.ExamSelectionBottomSheet sheet =
                    com.cynorra.kpssarena.presentation.mockexam.ExamSelectionBottomSheet.newInstance((examType, testNumber) -> {
                        android.os.Bundle args = new android.os.Bundle();
                        args.putString("examType", examType.name());
                        args.putInt("testNumber", testNumber);
                        Navigation.findNavController(v).navigate(R.id.action_home_to_mock_exam, args);
                    });
            sheet.show(getChildFragmentManager(), "ExamSelectionBottomSheet");
        });
        // Swipe refresh
        binding.swipeRefresh.setOnRefreshListener(() -> {
            viewModel.refreshData();
        });

        // Tactile Press Micro-interactions (Cult UI / StyleUI)
        AnimUtils.scalePressEffect(binding.cardStreak);
        AnimUtils.scalePressEffect(binding.cardDailyTest);
        AnimUtils.scalePressEffect(binding.btnStartTest);
        AnimUtils.scalePressEffect(binding.btnViewStats);
        AnimUtils.scalePressEffect(binding.btnViewMistakes);
        AnimUtils.scalePressEffect(binding.btnViewFlashcards);
        AnimUtils.scalePressEffect(binding.cardPractice);
        AnimUtils.scalePressEffect(binding.cardInvite);
        AnimUtils.scalePressEffect(binding.cardMockExam);

        // AdMob adaptive banner
        com.cynorra.kpssarena.presentation.common.AdManager.loadAdaptiveBanner(requireActivity(), binding.adBannerContainer);
    }
    @Override
    public void onResume() {
        super.onResume();
        bindEngagement();
    }
    /** Level / XP / daily goal / invite code come from on-device state, so refresh whenever we return here. */
    private void bindEngagement() {
        if (binding == null) return;
        android.content.Context ctx = requireContext();
        binding.tvLevel.setText(getString(R.string.home_level_format, com.cynorra.kpssarena.engagement.EngagementStore.getLevel(ctx)));
        int xpIn = com.cynorra.kpssarena.engagement.EngagementStore.getXpIntoLevel(ctx);
        int xpMax = com.cynorra.kpssarena.engagement.EngagementStore.getXpPerLevel();
        binding.tvXp.setText(getString(R.string.home_xp_format, xpIn, xpMax));
        binding.progressXp.setMax(xpMax);
        binding.progressXp.setProgress(xpIn);
        int done = com.cynorra.kpssarena.engagement.EngagementStore.getTodayAnswered(ctx);
        int goal = com.cynorra.kpssarena.engagement.EngagementStore.DAILY_GOAL;
        binding.tvGoal.setText(done >= goal ? getString(R.string.home_goal_done)
                : getString(R.string.home_goal_format, done, goal));
        binding.progressGoal.setMax(goal);
        binding.progressGoal.setProgress(Math.min(done, goal));
        binding.tvInviteCode.setText(getString(R.string.invite_subtitle,
                com.cynorra.kpssarena.engagement.InviteHelper.myCode()));
    }
    private void shareInvite() {
        String text = getString(R.string.invite_share_text,
                com.cynorra.kpssarena.engagement.InviteHelper.myCode(),
                com.cynorra.kpssarena.engagement.InviteHelper.inviteLink(requireContext()));
        com.cynorra.kpssarena.presentation.common.ShareHelper.shareAsPost(this, text, null);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    protected void observeData() {
        // User data
        viewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.tvUsername.setText(user.getUsername());
                binding.tvStreakCount.setText(String.valueOf(user.getStreak()));
                offerRemindersOnce();
                
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
