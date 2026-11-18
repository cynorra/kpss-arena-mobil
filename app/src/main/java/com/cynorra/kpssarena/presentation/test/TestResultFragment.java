package com.cynorra.kpssarena.presentation.test;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.airbnb.lottie.LottieDrawable;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentTestResultBinding;
import com.cynorra.kpssarena.domain.model.ScoreResult;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import com.cynorra.kpssarena.presentation.common.AnimUtils;
import com.cynorra.kpssarena.presentation.common.ConfettiHelper;
import com.cynorra.kpssarena.presentation.common.ShareCardGenerator;
import com.cynorra.kpssarena.presentation.common.ShareHelper;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class TestResultFragment extends BaseFragment<FragmentTestResultBinding, TestResultViewModel> {
    private boolean reminderOfferShown = false;
    private QuestionReviewAdapter reviewAdapter;
    private final com.cynorra.kpssarena.notification.ReminderPermissionRequester reminderPermission =
            new com.cynorra.kpssarena.notification.ReminderPermissionRequester(this, allowed -> {
                if (!isAdded()) return;
                com.cynorra.kpssarena.notification.ReminderScheduler.setEnabled(requireContext(), allowed);
                if (allowed) {
                    android.widget.Toast.makeText(getContext(), R.string.notif_reminder_on, android.widget.Toast.LENGTH_SHORT).show();
                }
            });
    private void offerDailyReminder() {
        if (reminderOfferShown || !isAdded()) return;
        android.content.Context context = requireContext();
        if (com.cynorra.kpssarena.notification.ReminderScheduler.wasPrompted(context)
                || com.cynorra.kpssarena.notification.ReminderScheduler.isEnabled(context)) {
            return;
        }
        reminderOfferShown = true;
        com.cynorra.kpssarena.notification.ReminderScheduler.markPrompted(context);
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(context)
                .setTitle(R.string.notif_offer_title)
                .setMessage(R.string.notif_offer_message)
                .setPositiveButton(R.string.notif_offer_yes, (d, w) -> reminderPermission.request())
                .setNegativeButton(R.string.notif_offer_no, null)
                .show();
    }
    @Override

    protected FragmentTestResultBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentTestResultBinding.inflate(inflater, container, false);
    }
    @Override
    protected Class<TestResultViewModel> getViewModelClass() {
        return TestResultViewModel.class;
    }
    @Override
    protected void initViews() {
        if (getArguments() != null) {
            @SuppressWarnings("deprecation")
            ScoreResult result = (ScoreResult) getArguments().getSerializable("scoreResult");
            if (result != null) {
                viewModel.setScoreResult(result);
            }
        }
        // Lottie confetti animation
        binding.lottieConfetti.setAnimation("confetti.json");
        binding.lottieConfetti.setRepeatCount(LottieDrawable.INFINITE);
        binding.lottieConfetti.playAnimation();

        // Review RecyclerView setup
        reviewAdapter = new QuestionReviewAdapter();
        binding.rvReviewQuestions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvReviewQuestions.setAdapter(reviewAdapter);

        // Filter chips setup
        binding.chipFilterWrong.setOnClickListener(v -> reviewAdapter.applyFilter(QuestionReviewAdapter.FilterMode.WRONG_ONLY));
        binding.chipFilterAll.setOnClickListener(v -> reviewAdapter.applyFilter(QuestionReviewAdapter.FilterMode.ALL));
        binding.chipFilterEmpty.setOnClickListener(v -> reviewAdapter.applyFilter(QuestionReviewAdapter.FilterMode.EMPTY_ONLY));
        binding.chipFilterCorrect.setOnClickListener(v -> reviewAdapter.applyFilter(QuestionReviewAdapter.FilterMode.CORRECT_ONLY));

        // Buttons
        binding.btnGoToMistakes.setOnClickListener(v -> {
            NavDirections action = TestResultFragmentDirections.actionResultToMistakes();
            Navigation.findNavController(v).navigate(action);
        });
        binding.btnBackToHome.setOnClickListener(v -> {
            NavDirections action = TestResultFragmentDirections.actionResultToHome();
            Navigation.findNavController(v).navigate(action);
        });
        binding.btnViewLeaderboard.setOnClickListener(v -> {
            NavDirections action = TestResultFragmentDirections.actionResultToLeaderboard();
            Navigation.findNavController(v).navigate(action);
        });
        binding.btnShare.setOnClickListener(v -> shareResult());
        binding.btnShareStory.setOnClickListener(v -> shareResultToStory());

        AnimUtils.scalePressEffect(binding.btnGoToMistakes);
        AnimUtils.scalePressEffect(binding.btnBackToHome);
        AnimUtils.scalePressEffect(binding.btnViewLeaderboard);
        AnimUtils.scalePressEffect(binding.btnShare);
        AnimUtils.scalePressEffect(binding.btnShareStory);
    }
    @Override
    protected void observeData() {
        viewModel.getScoreResult().observe(getViewLifecycleOwner(), this::displayResults);
        viewModel.getRank().observe(getViewLifecycleOwner(), this::displayRank);
    }
    private void displayResults(ScoreResult result) {
        if (result == null) return;

        // Celebratory Confetti Explosion
        if (result.getTotalScore() > 0) {
            ConfettiHelper.celebrate(requireActivity());
        }

        // Animate numbers counting up

        animateCounter(binding.tvCorrectCount, 0, result.getCorrectCount(), 1000);
        animateCounter(binding.tvWrongCount, 0, result.getWrongCount(), 1000);
        animateCounter(binding.tvEmptyCount, 0, result.getEmptyCount(), 1000);
        animateCounter(binding.tvNetScore, 0, result.getNetScore(), 1500);
        animateCounter(binding.tvSpeedBonus, 0, result.getSpeedBonus(), 1500);
        animateCounter(binding.tvTotalScore, 0, result.getTotalScore(), 2000);
        binding.tvAccuracy.setText(String.format("%% %.1f", result.getAccuracy()));

        // Display question review
        if (result.getQuestions() != null && !result.getQuestions().isEmpty()) {
            binding.layoutReviewSection.setVisibility(View.VISIBLE);
            reviewAdapter.setQuestions(result.getQuestions());

            int wrongCount = reviewAdapter.getCountForFilter(QuestionReviewAdapter.FilterMode.WRONG_ONLY);
            int allCount = reviewAdapter.getCountForFilter(QuestionReviewAdapter.FilterMode.ALL);
            int emptyCount = reviewAdapter.getCountForFilter(QuestionReviewAdapter.FilterMode.EMPTY_ONLY);
            int correctCount = reviewAdapter.getCountForFilter(QuestionReviewAdapter.FilterMode.CORRECT_ONLY);

            binding.chipFilterWrong.setText(getString(R.string.filter_wrong_format, wrongCount));
            binding.chipFilterAll.setText(getString(R.string.filter_all_format, allCount));
            binding.chipFilterEmpty.setText(getString(R.string.filter_empty_format, emptyCount));
            binding.chipFilterCorrect.setText(getString(R.string.filter_correct_format, correctCount));

            // Default selection: If there are mistakes, select mistakes directly!
            if (wrongCount > 0) {
                binding.chipFilterWrong.setChecked(true);
                reviewAdapter.applyFilter(QuestionReviewAdapter.FilterMode.WRONG_ONLY);
                binding.btnGoToMistakes.setVisibility(View.VISIBLE);
            } else if (emptyCount > 0) {
                binding.chipFilterEmpty.setChecked(true);
                reviewAdapter.applyFilter(QuestionReviewAdapter.FilterMode.EMPTY_ONLY);
                binding.btnGoToMistakes.setVisibility(View.VISIBLE);
            } else {
                binding.chipFilterAll.setChecked(true);
                reviewAdapter.applyFilter(QuestionReviewAdapter.FilterMode.ALL);
                binding.btnGoToMistakes.setVisibility(View.GONE);
            }
        } else {
            binding.layoutReviewSection.setVisibility(View.GONE);
            binding.btnGoToMistakes.setVisibility(View.GONE);
        }

        // Emoji based on score
        int emojiRes;
        int total = result.getTotalScore();
        if (total >= 45) emojiRes = R.string.result_emoji_excellent;
        else if (total >= 35) emojiRes = R.string.result_emoji_great;
        else if (total >= 25) emojiRes = R.string.result_emoji_good;
        else if (total >= 15) emojiRes = R.string.result_emoji_okay;
        else emojiRes = R.string.result_emoji_needs_work;
        binding.tvResultEmoji.setText(emojiRes);
        // Show interstitial ad for free users
        showInterstitialIfNeeded();
        offerDailyReminder();
    }
    private void displayRank(Integer rank) {
        if (rank != null) {
            binding.tvRank.setText(String.format("#%d", rank));
            
            // Top 3 special effects
            if (rank <= 3) {
                binding.cardRank.setStrokeColor(getResources().getColor(
                    rank == 1 ? R.color.gold_rank :
                    rank == 2 ? R.color.silver_rank : R.color.bronze_rank, null));
                binding.cardRank.setStrokeWidth(3);
                binding.ivRankMedal.setColorFilter(getResources().getColor(
                    rank == 1 ? R.color.gold_rank :
                    rank == 2 ? R.color.silver_rank : R.color.bronze_rank, null));
            }
        }
    }

    private void animateCounter(android.widget.TextView textView, int start, int end, int duration) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(duration);
        animator.addUpdateListener(animation -> 
            textView.setText(String.valueOf(animation.getAnimatedValue())));
        animator.start();
    }
    private void shareResult() {
        ScoreResult result = viewModel.getScoreResult().getValue();
        if (result == null) return;
        String shareText = getString(R.string.share_result_template,
            result.getNetScore(), result.getSpeedBonus(), result.getTotalScore(), com.cynorra.kpssarena.engagement.InviteHelper.inviteLink(requireContext()));
        ShareHelper.shareAsPost(this, shareText, null);
    }
    private void shareResultToStory() {
        ScoreResult result = viewModel.getScoreResult().getValue();
        if (result == null) return;
        android.graphics.Bitmap card = ShareCardGenerator.generateStoryCard(requireContext(),
                getString(R.string.share_story_test_title),
                String.valueOf(result.getTotalScore()),
                getString(R.string.share_story_score_label),
                getString(R.string.share_story_cta),
                getString(R.string.invite_line, com.cynorra.kpssarena.engagement.InviteHelper.myCode()));
        ShareHelper.shareToStory(this, card);
    }
    private void showInterstitialIfNeeded() {
        if (viewModel.isPremiumUser()) {
            return;
        }
        com.cynorra.kpssarena.presentation.common.AdManager.showInterstitial(requireActivity(), null);
    }
}
