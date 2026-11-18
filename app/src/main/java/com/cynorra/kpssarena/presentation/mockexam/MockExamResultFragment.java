package com.cynorra.kpssarena.presentation.mockexam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentMockExamResultBinding;
import com.cynorra.kpssarena.domain.model.ScoreResult;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import com.cynorra.kpssarena.presentation.common.AnimUtils;
import com.cynorra.kpssarena.presentation.common.ConfettiHelper;
import com.cynorra.kpssarena.presentation.common.ShareCardGenerator;
import com.cynorra.kpssarena.presentation.common.ShareHelper;
import com.cynorra.kpssarena.presentation.test.QuestionReviewAdapter;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MockExamResultFragment extends BaseFragment<FragmentMockExamResultBinding, MockExamResultViewModel> {

    private QuestionReviewAdapter reviewAdapter;

    @javax.inject.Inject
    com.cynorra.kpssarena.domain.repository.MatchRepository matchRepository;

    @Override
    protected FragmentMockExamResultBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentMockExamResultBinding.inflate(inflater, container, false);
    }

    @Override
    protected Class<MockExamResultViewModel> getViewModelClass() {
        return MockExamResultViewModel.class;
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

        // Review Adapter setup
        reviewAdapter = new QuestionReviewAdapter();
        binding.rvReviewQuestions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvReviewQuestions.setAdapter(reviewAdapter);

        // Filter chips setup
        binding.chipFilterWrong.setOnClickListener(v -> reviewAdapter.applyFilter(QuestionReviewAdapter.FilterMode.WRONG_ONLY));
        binding.chipFilterAll.setOnClickListener(v -> reviewAdapter.applyFilter(QuestionReviewAdapter.FilterMode.ALL));
        binding.chipFilterEmpty.setOnClickListener(v -> reviewAdapter.applyFilter(QuestionReviewAdapter.FilterMode.EMPTY_ONLY));
        binding.chipFilterCorrect.setOnClickListener(v -> reviewAdapter.applyFilter(QuestionReviewAdapter.FilterMode.CORRECT_ONLY));

        binding.btnGoToMistakes.setOnClickListener(v -> {
            NavDirections action = MockExamResultFragmentDirections.actionMockExamResultToMistakes();
            Navigation.findNavController(v).navigate(action);
        });

        binding.btnChallengeInArena.setOnClickListener(v -> {
            ScoreResult res = viewModel.getScoreResult().getValue();
            if (res != null && matchRepository != null) {
                String examTypeStr = res.getExamType() != null ? res.getExamType() : "FULL_KPSS";
                matchRepository.createExamDuel(examTypeStr, res)
                        .addOnSuccessListener(matchId -> {
                            android.widget.Toast.makeText(getContext(), "⚔️ Düello odası açıldı!", android.widget.Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(v).navigate(R.id.action_mock_exam_result_to_arena);
                        })
                        .addOnFailureListener(e -> {
                            android.widget.Toast.makeText(getContext(), "Oda açılamadı: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                        });
            }
        });

        binding.btnBackToHome.setOnClickListener(v -> {
            NavDirections action = MockExamResultFragmentDirections.actionMockExamResultToHome();
            Navigation.findNavController(v).navigate(action);
        });
        binding.btnShare.setOnClickListener(v -> shareResult());
        binding.btnShareStory.setOnClickListener(v -> shareResultToStory());

        AnimUtils.scalePressEffect(binding.btnGoToMistakes);
        AnimUtils.scalePressEffect(binding.btnChallengeInArena);
        AnimUtils.scalePressEffect(binding.btnBackToHome);
        AnimUtils.scalePressEffect(binding.btnShare);
        AnimUtils.scalePressEffect(binding.btnShareStory);
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
                getString(R.string.share_story_mock_exam_title),
                String.valueOf(result.getTotalScore()),
                getString(R.string.share_story_score_label),
                getString(R.string.share_story_cta),
                getString(R.string.invite_line, com.cynorra.kpssarena.engagement.InviteHelper.myCode()));
        ShareHelper.shareToStory(this, card);
    }

    @Override
    protected void observeData() {
        viewModel.getScoreResult().observe(getViewLifecycleOwner(), this::displayResults);
    }

    private void displayResults(ScoreResult result) {
        if (result == null) return;

        // Celebratory Confetti Explosion
        if (result.getTotalScore() > 0) {
            ConfettiHelper.celebrate(requireActivity());
        }

        // Animate counter
        AnimUtils.countUpAnimation(anim -> binding.tvCorrectCount.setText(String.valueOf(anim.getAnimatedValue())), 0, result.getCorrectCount(), 1000);
        AnimUtils.countUpAnimation(anim -> binding.tvWrongCount.setText(String.valueOf(anim.getAnimatedValue())), 0, result.getWrongCount(), 1000);
        AnimUtils.countUpAnimation(anim -> binding.tvEmptyCount.setText(String.valueOf(anim.getAnimatedValue())), 0, result.getEmptyCount(), 1000);
        AnimUtils.countUpAnimation(anim -> binding.tvNetScore.setText(String.valueOf(anim.getAnimatedValue())), 0, result.getNetScore(), 1400);
        AnimUtils.countUpAnimation(anim -> binding.tvTotalScore.setText(String.valueOf(anim.getAnimatedValue())), 0, result.getTotalScore(), 1800);
        binding.tvAccuracy.setText(String.format("%% %.1f", result.getAccuracy()));

        // Show interstitial ad after completing mock exam
        com.cynorra.kpssarena.presentation.common.AdManager.showInterstitial(requireActivity(), null);

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
    }
}
