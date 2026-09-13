package com.cynorra.kpssarena.presentation.mockexam;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentMockExamResultBinding;
import com.cynorra.kpssarena.domain.model.ScoreResult;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import com.cynorra.kpssarena.presentation.common.ShareCardGenerator;
import com.cynorra.kpssarena.presentation.common.ShareHelper;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class MockExamResultFragment extends BaseFragment<FragmentMockExamResultBinding, MockExamResultViewModel> {
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
        binding.btnBackToHome.setOnClickListener(v -> {
            NavDirections action = MockExamResultFragmentDirections.actionMockExamResultToHome();
            Navigation.findNavController(v).navigate(action);
        });
        binding.btnShare.setOnClickListener(v -> shareResult());
        binding.btnShareStory.setOnClickListener(v -> shareResultToStory());
    }
    private void shareResult() {
        ScoreResult result = viewModel.getScoreResult().getValue();
        if (result == null) return;
        String shareText = getString(R.string.share_result_template,
                result.getNetScore(), result.getSpeedBonus(), result.getTotalScore(), getString(R.string.play_store_url));
        ShareHelper.shareAsPost(this, shareText, null);
    }
    private void shareResultToStory() {
        ScoreResult result = viewModel.getScoreResult().getValue();
        if (result == null) return;
        android.graphics.Bitmap card = ShareCardGenerator.generateStoryCard(requireContext(),
                getString(R.string.share_story_mock_exam_title),
                String.valueOf(result.getTotalScore()),
                getString(R.string.share_story_score_label),
                getString(R.string.share_story_cta));
        ShareHelper.shareToStory(this, card);
    }
    @Override
    protected void observeData() {
        viewModel.getScoreResult().observe(getViewLifecycleOwner(), this::displayResults);
    }
    private void displayResults(ScoreResult result) {
        if (result == null) return;
        binding.tvCorrectCount.setText(String.valueOf(result.getCorrectCount()));
        binding.tvWrongCount.setText(String.valueOf(result.getWrongCount()));
        binding.tvEmptyCount.setText(String.valueOf(result.getEmptyCount()));
        binding.tvNetScore.setText(String.valueOf(result.getNetScore()));
        binding.tvTotalScore.setText(String.valueOf(result.getTotalScore()));
        binding.tvAccuracy.setText(String.format("%% %.1f", result.getAccuracy()));
    }
}
