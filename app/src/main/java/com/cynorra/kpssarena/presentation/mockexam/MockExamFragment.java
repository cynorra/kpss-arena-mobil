package com.cynorra.kpssarena.presentation.mockexam;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.navigation.Navigation;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentMockExamBinding;
import com.cynorra.kpssarena.domain.model.Question;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class MockExamFragment extends BaseFragment<FragmentMockExamBinding, MockExamViewModel> {
    @Override
    protected FragmentMockExamBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentMockExamBinding.inflate(inflater, container, false);
    }
    @Override
    protected Class<MockExamViewModel> getViewModelClass() {
        return MockExamViewModel.class;
    }
    @Override
    protected void initViews() {
        binding.btnOptionA.setOnClickListener(v -> selectAnswer("A"));
        binding.btnOptionB.setOnClickListener(v -> selectAnswer("B"));
        binding.btnOptionC.setOnClickListener(v -> selectAnswer("C"));
        binding.btnOptionD.setOnClickListener(v -> selectAnswer("D"));
        binding.btnPrevious.setOnClickListener(v -> viewModel.previousQuestion());
        binding.btnNext.setOnClickListener(v -> viewModel.nextQuestion());
        binding.btnSubmitExam.setOnClickListener(v -> showSubmitConfirmation());
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
    private void showSubmitConfirmation() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.action_finish_mock_exam)
                .setMessage(R.string.dialog_finish_test_message)
                .setPositiveButton(R.string.action_finish, (dialog, which) -> viewModel.submitExam())
                .setNegativeButton(R.string.action_continue, null)
                .show();
    }
    @Override
    protected void observeData() {
        viewModel.getCurrentQuestion().observe(getViewLifecycleOwner(), question -> {
            if (question != null) updateQuestionUI(question);
        });
        viewModel.getCurrentQuestionIndex().observe(getViewLifecycleOwner(), index -> {
            if (index != null) {
                binding.tvQuestionNumber.setText(getString(R.string.question_counter_format,
                        index + 1, viewModel.getTotalQuestions()));
                binding.progressBar.setProgress((index + 1) * 100 / Math.max(1, viewModel.getTotalQuestions()));
                binding.btnPrevious.setEnabled(index > 0);
                binding.btnNext.setEnabled(index < viewModel.getTotalQuestions() - 1);
            }
        });
        viewModel.getTimeRemaining().observe(getViewLifecycleOwner(), this::updateTimer);
        viewModel.getTimeUp().observe(getViewLifecycleOwner(), timeUp -> {
            if (timeUp != null && timeUp) {
                viewModel.submitExam();
            }
        });
        viewModel.getNavigateToResult().observe(getViewLifecycleOwner(), scoreResult -> {
            if (scoreResult != null) {
                Bundle args = new Bundle();
                args.putSerializable("scoreResult", scoreResult);
                Navigation.findNavController(requireView()).navigate(R.id.action_mock_exam_to_result, args);
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
        if (question.getUserAnswer() != null) {
            highlightSelectedButton(question.getUserAnswer());
        }
    }
    private void updateTimer(Long timeMs) {
        if (timeMs == null) return;
        int totalSeconds = (int) (timeMs / 1000);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        binding.tvTimer.setText(getString(R.string.timer_format_mm_ss, minutes, seconds));
        if (totalSeconds <= 300) {
            binding.tvTimer.setTextColor(getResources().getColor(R.color.error, null));
        } else {
            binding.tvTimer.setTextColor(getResources().getColor(R.color.primary, null));
        }
    }
}
