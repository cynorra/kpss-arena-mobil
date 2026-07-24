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
import com.airbnb.lottie.LottieDrawable;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentTestResultBinding;
import com.cynorra.kpssarena.domain.model.ScoreResult;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class TestResultFragment extends BaseFragment<FragmentTestResultBinding, TestResultViewModel> {
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
        // Lottie confetti animation
        binding.lottieConfetti.setAnimation("confetti.json");
        binding.lottieConfetti.setRepeatCount(LottieDrawable.INFINITE);
        binding.lottieConfetti.playAnimation();
        // Buttons
        binding.btnBackToHome.setOnClickListener(v -> {
            NavDirections action = TestResultFragmentDirections.actionResultToHome();
            Navigation.findNavController(v).navigate(action);
        });
        binding.btnViewLeaderboard.setOnClickListener(v -> {
            NavDirections action = TestResultFragmentDirections.actionResultToLeaderboard();
            Navigation.findNavController(v).navigate(action);
        });
        binding.btnShare.setOnClickListener(v -> shareResult());
    }
    @Override
    protected void observeData() {
        viewModel.getScoreResult().observe(getViewLifecycleOwner(), this::displayResults);
        viewModel.getRank().observe(getViewLifecycleOwner(), this::displayRank);
    }
    private void displayResults(ScoreResult result) {
        if (result == null) return;
        // Animate numbers counting up

        animateCounter(binding.tvCorrectCount, 0, result.getCorrectCount(), 1000);
        animateCounter(binding.tvWrongCount, 0, result.getWrongCount(), 1000);
        animateCounter(binding.tvEmptyCount, 0, result.getEmptyCount(), 1000);
        animateCounter(binding.tvNetScore, 0, result.getNetScore(), 1500);
        animateCounter(binding.tvSpeedBonus, 0, result.getSpeedBonus(), 1500);
        animateCounter(binding.tvTotalScore, 0, result.getTotalScore(), 2000);
        binding.tvAccuracy.setText(String.format("%% %.1f", result.getAccuracy()));
        // Emoji based on score
        String emoji;
        int total = result.getTotalScore();
        if (total >= 45) emoji = "🏆 Mükemmel!";
        else if (total >= 35) emoji = "🌟 Harika!";
        else if (total >= 25) emoji = "👏 İyi İş!";
        else if (total >= 15) emoji = "💪 İdare Eder";
        else emoji = "📚 Daha Çok Çalış";
        binding.tvResultEmoji.setText(emoji);
        // Show interstitial ad for free users
        showInterstitialIfNeeded();
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
                
                String medal = rank == 1 ? "🥇" : rank == 2 ? "🥈" : "🥉";
                binding.tvRankMedal.setText(medal);
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
        String shareText = String.format(
            "🔥 KPSS Arena'da bugün %d net yaptım!\n" +
            "⏱️ Hız Bonusu: +%d\n" +
            "⭐ Toplam: %d puan\n\n" +
            "Sen de dene: [link]",
            result.getNetScore(), result.getSpeedBonus(), result.getTotalScore()
        );
        android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
        startActivity(android.content.Intent.createChooser(shareIntent, "Paylaş"));
    }
    private void showInterstitialIfNeeded() {
        // AdMob interstitial logic (show max 3 times per day)
        com.google.android.gms.ads.interstitial.InterstitialAd.load(requireContext(),
            "ca-app-pub-3940256099942544/1033173712",
            new com.google.android.gms.ads.AdRequest.Builder().build(),
            new com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd ad) {
                    if (!viewModel.isPremiumUser()) {
                        ad.show(requireActivity());
                    }
                }
            });

    }
}
