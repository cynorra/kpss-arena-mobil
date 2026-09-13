package com.cynorra.kpssarena.presentation.test;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentTestBinding;
import com.cynorra.kpssarena.domain.model.Question;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import com.cynorra.kpssarena.presentation.common.CountdownView;
import java.util.Locale;
import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class TestFragment extends BaseFragment<FragmentTestBinding, TestViewModel> {
    private CountdownView countdownView;
    private ObjectAnimator pulseAnimator;
    @Override
    protected FragmentTestBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentTestBinding.inflate(inflater, container, false);
    }
    @Override
    protected Class<TestViewModel> getViewModelClass() {
        return TestViewModel.class;
    }
    @Override
    protected void initViews() {
        countdownView = binding.countdownTimer;
        
        binding.btnOptionA.setOnClickListener(v -> selectAnswer("A"));
        binding.btnOptionB.setOnClickListener(v -> selectAnswer("B"));
        binding.btnOptionC.setOnClickListener(v -> selectAnswer("C"));
        binding.btnOptionD.setOnClickListener(v -> selectAnswer("D"));

        
        binding.btnNext.setOnClickListener(v -> viewModel.nextQuestion());
        binding.btnPrevious.setOnClickListener(v -> viewModel.previousQuestion());
        binding.btnSubmitTest.setOnClickListener(v -> showSubmitConfirmation());
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
                .setTitle(R.string.action_finish_test)
                .setMessage(R.string.dialog_finish_test_message)
                .setPositiveButton(R.string.action_finish, (dialog, which) -> viewModel.submitTest())
                .setNegativeButton(R.string.action_continue, null)
                .show();

    }
    @Override
    protected void observeData() {
        viewModel.getCurrentQuestion().observe(getViewLifecycleOwner(), question -> {
            if (question != null) {
                updateQuestionUI(question);
            }
        });
        viewModel.getCurrentQuestionIndex().observe(getViewLifecycleOwner(), index -> {
            if (index != null) {
                binding.tvQuestionNumber.setText(getString(R.string.question_counter_format,
                    index + 1, viewModel.getTotalQuestions()));
                binding.progressBar.setProgress((index + 1) * 100 / viewModel.getTotalQuestions());
                
                binding.btnPrevious.setEnabled(index > 0);
                binding.btnNext.setEnabled(index < viewModel.getTotalQuestions() - 1);
            }
        });
        viewModel.getTimeRemaining().observe(getViewLifecycleOwner(), timeMs -> {
            if (timeMs != null) {
                updateCountdown(timeMs);
            }
        });
        viewModel.getNavigateToResult().observe(getViewLifecycleOwner(), scoreResult -> {
            if (scoreResult != null) {
                Bundle args = new Bundle();
                args.putSerializable("scoreResult", scoreResult);
                Navigation.findNavController(requireView()).navigate(R.id.action_test_to_result, args);
            }
        });
        viewModel.getTimeUp().observe(getViewLifecycleOwner(), timeUp -> {
            if (timeUp != null && timeUp) {
                viewModel.submitTest();
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
        // Spring animation for question transition
        animateQuestionChange();
    }
    private void animateQuestionChange() {
        binding.cardQuestion.setTranslationX(50f);
        binding.cardQuestion.setAlpha(0f);
        binding.cardQuestion.animate()
                .translationX(0f)
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }
    private void updateCountdown(long timeMs) {
        int seconds = (int) (timeMs / 1000);
        binding.tvTimer.setText(String.format(Locale.getDefault(), "%d", seconds));
        countdownView.setProgress((float) timeMs / (45 * 1000));
        if (seconds <= 10) {
            binding.tvTimer.setTextColor(getResources().getColor(R.color.error, null));
            startPulseAnimation();
        } else {
            binding.tvTimer.setTextColor(getResources().getColor(R.color.primary, null));
            stopPulseAnimation();
        }

    }
    private void startPulseAnimation() {
        if (pulseAnimator == null) {
            pulseAnimator = ObjectAnimator.ofFloat(binding.tvTimer, "scaleX", 1f, 1.2f, 1f);
            pulseAnimator.setDuration(1000);
            pulseAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        }
        if (!pulseAnimator.isRunning()) {
            pulseAnimator.start();
        }
    }
    private void stopPulseAnimation() {
        if (pulseAnimator != null && pulseAnimator.isRunning()) {
            pulseAnimator.cancel();
            binding.tvTimer.setScaleX(1f);
        }
    }
    @Override
    public void onDestroyView() {
        stopPulseAnimation();
        super.onDestroyView();
    }
}
