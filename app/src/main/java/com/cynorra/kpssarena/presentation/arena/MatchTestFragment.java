package com.cynorra.kpssarena.presentation.arena;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.navigation.Navigation;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentMatchTestBinding;
import com.cynorra.kpssarena.domain.model.Question;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import java.util.Locale;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class MatchTestFragment extends BaseFragment<FragmentMatchTestBinding, MatchTestViewModel> {
    private String matchId;
    @Override
    protected FragmentMatchTestBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentMatchTestBinding.inflate(inflater, container, false);
    }
    @Override
    protected Class<MatchTestViewModel> getViewModelClass() {
        return MatchTestViewModel.class;
    }
    @Override
    protected void initViews() {
        matchId = requireArguments().getString("matchId");
        binding.btnOptionA.setOnClickListener(v -> selectAnswer("A"));
        binding.btnOptionB.setOnClickListener(v -> selectAnswer("B"));
        binding.btnOptionC.setOnClickListener(v -> selectAnswer("C"));
        binding.btnOptionD.setOnClickListener(v -> selectAnswer("D"));
        binding.btnNext.setOnClickListener(v -> viewModel.nextQuestion());
        viewModel.loadMatch(matchId);
    }
    private void selectAnswer(String answer) {
        viewModel.answerCurrentQuestion(answer);
        highlightSelectedButton(answer);
    }
    private void highlightSelectedButton(String answer) {
        resetButtonStyles();
        switch (answer) {
            case "A": setButtonSelected(binding.btnOptionA); break;
            case "B": setButtonSelected(binding.btnOptionB); break;
            case "C": setButtonSelected(binding.btnOptionC); break;
            case "D": setButtonSelected(binding.btnOptionD); break;
        }
    }
    private void setButtonSelected(com.google.android.material.button.MaterialButton button) {
        button.setStrokeColorResource(R.color.primary);
        button.setStrokeWidth(3);
    }
    private void resetButtonStyles() {
        binding.btnOptionA.setStrokeWidth(0);
        binding.btnOptionB.setStrokeWidth(0);
        binding.btnOptionC.setStrokeWidth(0);
        binding.btnOptionD.setStrokeWidth(0);
    }
    @Override
    protected void observeData() {
        viewModel.getCurrentQuestion().observe(getViewLifecycleOwner(), question -> {
            if (question != null) updateQuestionUI(question);
        });
        viewModel.getCurrentQuestionIndex().observe(getViewLifecycleOwner(), index -> {
            if (index != null) {
                int total = Math.max(1, viewModel.getTotalQuestions());
                binding.tvQuestionNumber.setText(getString(R.string.question_counter_format,
                        index + 1, viewModel.getTotalQuestions()));
                binding.progressBar.setProgress((index + 1) * 100 / total);
                binding.btnNext.setText(index == viewModel.getTotalQuestions() - 1
                        ? R.string.action_finish : R.string.action_next);
            }
        });
        viewModel.getTimeRemaining().observe(getViewLifecycleOwner(), timeMs -> {
            if (timeMs != null) {
                int seconds = (int) (timeMs / 1000);
                binding.tvTimer.setText(String.format(Locale.getDefault(), "%d", seconds));
            }
        });
        viewModel.getMyScore().observe(getViewLifecycleOwner(), score -> {
            if (score != null) binding.tvMyScore.setText(getString(R.string.score_you_format, score));
        });
        viewModel.getOpponentScore().observe(getViewLifecycleOwner(), score -> {
            if (score != null) binding.tvOpponentScore.setText(getString(R.string.score_opponent_format, score));
        });
        viewModel.getNavigateToResult().observe(getViewLifecycleOwner(), navigate -> {
            if (navigate != null && navigate) {
                Bundle args = new Bundle();
                args.putString("matchId", matchId);
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_match_test_to_result, args);
            }
        });
    }
    private void updateQuestionUI(Question question) {
        binding.tvQuestionText.setText(question.getText());
        binding.btnOptionA.setText(getString(R.string.option_label_a) + " " + question.getOptionA());
        binding.btnOptionB.setText(getString(R.string.option_label_b) + " " + question.getOptionB());
        binding.btnOptionC.setText(getString(R.string.option_label_c) + " " + question.getOptionC());
        binding.btnOptionD.setText(getString(R.string.option_label_d) + " " + question.getOptionD());
        resetButtonStyles();
    }
}
