package com.cynorra.kpssarena.presentation.test;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentTestBinding;
import com.cynorra.kpssarena.domain.model.Question;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import com.cynorra.kpssarena.presentation.common.CountdownView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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

        // Exit / Back handling
        binding.btnExitTest.setOnClickListener(v -> showExitConfirmation());
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitConfirmation();
            }
        });

        // Option cards click
        binding.cardOptionA.setOnClickListener(v -> animateAndSelect("A", binding.cardOptionA));
        binding.cardOptionB.setOnClickListener(v -> animateAndSelect("B", binding.cardOptionB));
        binding.cardOptionC.setOnClickListener(v -> animateAndSelect("C", binding.cardOptionC));
        binding.cardOptionD.setOnClickListener(v -> animateAndSelect("D", binding.cardOptionD));

        // Navigation buttons
        binding.btnPrevious.setOnClickListener(v -> viewModel.previousQuestion());
        binding.btnNext.setOnClickListener(v -> {
            Integer currentIndex = viewModel.getCurrentQuestionIndex().getValue();
            int total = viewModel.getTotalQuestions();
            if (currentIndex != null && total > 0 && currentIndex >= total - 1) {
                showSubmitConfirmation();
            } else {
                viewModel.nextQuestion();
            }
        });
        binding.btnSubmitEarly.setOnClickListener(v -> showSubmitConfirmation());
        binding.btnJokerFifty.setOnClickListener(v -> showJokerDialog());
    }

    private void showJokerDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.action_joker_fifty)
                .setMessage(R.string.dialog_joker_reward_message)
                .setPositiveButton(R.string.action_watch_ad, (dialog, which) -> {
                    com.cynorra.kpssarena.presentation.common.AdManager.showRewardedAd(requireActivity(), this::eliminateTwoWrongOptions);
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    private void eliminateTwoWrongOptions() {
        Question q = viewModel.getCurrentQuestion().getValue();
        if (q == null || q.getCorrectAnswer() == null) return;
        String correct = q.getCorrectAnswer();
        java.util.List<String> wrong = new java.util.ArrayList<>();
        for (String opt : new String[]{"A", "B", "C", "D"}) {
            if (!opt.equalsIgnoreCase(correct)) {
                wrong.add(opt);
            }
        }
        java.util.Collections.shuffle(wrong);
        for (int i = 0; i < Math.min(2, wrong.size()); i++) {
            eliminateOption(wrong.get(i));
        }
    }

    private void eliminateOption(String option) {
        MaterialCardView card = null;
        TextView text = null;
        if ("A".equalsIgnoreCase(option)) {
            card = binding.cardOptionA;
            text = binding.tvOptionAText;
        } else if ("B".equalsIgnoreCase(option)) {
            card = binding.cardOptionB;
            text = binding.tvOptionBText;
        } else if ("C".equalsIgnoreCase(option)) {
            card = binding.cardOptionC;
            text = binding.tvOptionCText;
        } else if ("D".equalsIgnoreCase(option)) {
            card = binding.cardOptionD;
            text = binding.tvOptionDText;
        }
        if (card != null && text != null) {
            card.setEnabled(false);
            card.setAlpha(0.35f);
            text.setPaintFlags(text.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    private void animateAndSelect(String answer, MaterialCardView card) {
        card.animate().scaleX(0.97f).scaleY(0.97f).setDuration(70).withEndAction(() -> {
            card.animate().scaleX(1.0f).scaleY(1.0f).setDuration(70).start();
            selectAnswer(answer);
        }).start();
    }

    private void selectAnswer(String answer) {
        viewModel.answerCurrentQuestion(answer);
        highlightSelectedOption(answer);
    }

    private void highlightSelectedOption(String answer) {
        resetOptionCardStyle(binding.cardOptionA, binding.tvLetterA, binding.tvOptionAText);
        resetOptionCardStyle(binding.cardOptionB, binding.tvLetterB, binding.tvOptionBText);
        resetOptionCardStyle(binding.cardOptionC, binding.tvLetterC, binding.tvOptionCText);
        resetOptionCardStyle(binding.cardOptionD, binding.tvLetterD, binding.tvOptionDText);

        if ("A".equalsIgnoreCase(answer)) {
            setSelectedOptionCardStyle(binding.cardOptionA, binding.tvLetterA, binding.tvOptionAText);
        } else if ("B".equalsIgnoreCase(answer)) {
            setSelectedOptionCardStyle(binding.cardOptionB, binding.tvLetterB, binding.tvOptionBText);
        } else if ("C".equalsIgnoreCase(answer)) {
            setSelectedOptionCardStyle(binding.cardOptionC, binding.tvLetterC, binding.tvOptionCText);
        } else if ("D".equalsIgnoreCase(answer)) {
            setSelectedOptionCardStyle(binding.cardOptionD, binding.tvLetterD, binding.tvOptionDText);
        }
    }

    private void setSelectedOptionCardStyle(MaterialCardView card, TextView letter, TextView text) {
        int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary);
        int onPrimaryColor = ContextCompat.getColor(requireContext(), R.color.on_primary);

        card.setStrokeColor(primaryColor);
        card.setStrokeWidth(dpToPx(2f));
        card.setCardBackgroundColor(Color.parseColor("#262014"));

        letter.setBackgroundResource(R.drawable.bg_option_letter_selected);
        letter.setTextColor(onPrimaryColor);

        text.setTextColor(primaryColor);
    }

    private void resetOptionCardStyle(MaterialCardView card, TextView letter, TextView text) {
        int outlineColor = ContextCompat.getColor(requireContext(), R.color.outline);
        int surfaceVariant = ContextCompat.getColor(requireContext(), R.color.surface_variant);
        int onSurface = ContextCompat.getColor(requireContext(), R.color.on_surface);

        card.setStrokeColor(outlineColor);
        card.setStrokeWidth(dpToPx(1f));
        card.setCardBackgroundColor(surfaceVariant);
        card.setEnabled(true);
        card.setAlpha(1.0f);

        letter.setBackgroundResource(R.drawable.bg_option_letter_default);
        letter.setTextColor(onSurface);

        text.setTextColor(onSurface);
        text.setPaintFlags(text.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
    }

    private int dpToPx(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void showSubmitConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.action_finish_test)
                .setMessage(R.string.dialog_finish_test_message)
                .setPositiveButton(R.string.action_finish, (dialog, which) -> viewModel.submitTest())
                .setNegativeButton(R.string.action_continue, null)
                .show();
    }

    private void showExitConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_exit_test_title)
                .setMessage(R.string.dialog_exit_test_message)
                .setPositiveButton(R.string.action_exit, (dialog, which) -> {
                    Navigation.findNavController(requireView()).navigateUp();
                })
                .setNegativeButton(R.string.action_continue, null)
                .show();
    }

    @Override
    protected void observeData() {
        viewModel.getCurrentQuestion().observe(getViewLifecycleOwner(), question -> {
            if (question != null) {
                binding.cardQuestion.animate().alpha(0.3f).setDuration(80).withEndAction(() -> {
                    updateQuestionUI(question);
                    binding.cardQuestion.animate().alpha(1.0f).setDuration(120).start();
                }).start();
            }
        });

        viewModel.getCurrentQuestionIndex().observe(getViewLifecycleOwner(), index -> {
            if (index != null) {
                int total = viewModel.getTotalQuestions();
                binding.tvQuestionNumber.setText(getString(R.string.question_counter_format,
                        index + 1, total > 0 ? total : 12));
                if (total > 0) {
                    binding.progressBar.setProgress((index + 1) * 100 / total);
                }

                binding.btnPrevious.setEnabled(index > 0);

                if (total > 0 && index >= total - 1) {
                    binding.btnNext.setText(R.string.action_finish_and_see_results);
                    binding.btnNext.setIconResource(R.drawable.ic_check);
                    binding.btnSubmitEarly.setVisibility(View.GONE);
                } else {
                    binding.btnNext.setText(R.string.action_next_question);
                    binding.btnNext.setIconResource(R.drawable.ic_arrow_forward);
                    binding.btnSubmitEarly.setVisibility(View.VISIBLE);
                }
            }
        });

        viewModel.getTimeRemaining().observe(getViewLifecycleOwner(), timeMs -> {
            if (timeMs != null) {
                updateCountdown(timeMs);
            }
        });

        viewModel.getNavigateToResult().observe(getViewLifecycleOwner(), scoreResult -> {
            if (scoreResult != null) {
                com.cynorra.kpssarena.engagement.EngagementStore.addXp(requireContext(),
                        com.cynorra.kpssarena.engagement.EngagementStore.XP_DAILY_TEST_BASE
                                + scoreResult.getCorrectCount() * com.cynorra.kpssarena.engagement.EngagementStore.XP_DAILY_TEST_PER_CORRECT);
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
        String category = question.getCategory() != null ? question.getCategory() : "Genel";
        String categoryWithIcon = formatCategoryWithIcon(category);
        binding.tvCategoryChip.setText(categoryWithIcon);
        binding.tvCategoryHeader.setText(categoryWithIcon);

        binding.tvQuestionText.setText(question.getText());
        binding.tvOptionAText.setText(question.getOptionA());
        binding.tvOptionBText.setText(question.getOptionB());
        binding.tvOptionCText.setText(question.getOptionC());
        binding.tvOptionDText.setText(question.getOptionD());

        highlightSelectedOption(question.getUserAnswer());
        animateQuestionChange();
    }

    private String formatCategoryWithIcon(String category) {
        if (category == null) return "Genel";
        String cat = category.trim();
        if (cat.equalsIgnoreCase("Tarih")) return "📚 Tarih";
        if (cat.equalsIgnoreCase("Matematik")) return "📐 Matematik";
        if (cat.equalsIgnoreCase("Türkçe")) return "📝 Türkçe";
        if (cat.equalsIgnoreCase("Coğrafya")) return "🌍 Coğrafya";
        if (cat.equalsIgnoreCase("Vatandaşlık")) return "⚖️ Vatandaşlık";
        if (cat.equalsIgnoreCase("Güncel Bilgiler")) return "💡 Güncel Bilgiler";
        return "📌 " + cat;
    }

    private void animateQuestionChange() {
        binding.cardQuestion.setTranslationX(40f);
        binding.cardQuestion.setAlpha(0f);
        binding.cardQuestion.animate()
                .translationX(0f)
                .alpha(1f)
                .setDuration(260)
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
