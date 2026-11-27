package com.cynorra.kpssarena.presentation.mockexam;

import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentMockExamBinding;
import com.cynorra.kpssarena.domain.model.ExamType;
import com.cynorra.kpssarena.domain.model.Question;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import com.cynorra.kpssarena.presentation.common.AdManager;
import com.cynorra.kpssarena.presentation.common.AnimUtils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        showExitConfirmation();
                    }
                });
    }

    @Override
    protected void initViews() {
        binding.cardOptionA.setOnClickListener(v -> animateAndSelect("A", binding.cardOptionA));
        binding.cardOptionB.setOnClickListener(v -> animateAndSelect("B", binding.cardOptionB));
        binding.cardOptionC.setOnClickListener(v -> animateAndSelect("C", binding.cardOptionC));
        binding.cardOptionD.setOnClickListener(v -> animateAndSelect("D", binding.cardOptionD));
        binding.cardOptionE.setOnClickListener(v -> animateAndSelect("E", binding.cardOptionE));

        AnimUtils.scalePressEffect(binding.cardOptionA);
        AnimUtils.scalePressEffect(binding.cardOptionB);
        AnimUtils.scalePressEffect(binding.cardOptionC);
        AnimUtils.scalePressEffect(binding.cardOptionD);
        AnimUtils.scalePressEffect(binding.cardOptionE);

        binding.btnFlagQuestion.setOnClickListener(v -> viewModel.toggleFlagCurrentQuestion());
        binding.tvQuestionNumber.setOnClickListener(v -> showQuestionPaletteDialog());
        binding.btnPalette.setOnClickListener(v -> showQuestionPaletteDialog());
        AnimUtils.scalePressEffect(binding.btnFlagQuestion);

        binding.btnPrevious.setOnClickListener(v -> viewModel.previousQuestion());
        binding.btnNext.setOnClickListener(v -> viewModel.nextQuestion());

        binding.btnExitExam.setOnClickListener(v -> showExitConfirmation());
        binding.btnSubmitExam.setOnClickListener(v -> showSubmitConfirmation());

        binding.btnJokerFifty.setOnClickListener(v -> showJokerDialog());

        AnimUtils.scalePressEffect(binding.btnPrevious);
        AnimUtils.scalePressEffect(binding.btnNext);
        AnimUtils.scalePressEffect(binding.btnSubmitExam);
        AnimUtils.scalePressEffect(binding.btnJokerFifty);
    }

    private void animateAndSelect(String answer, MaterialCardView card) {
        AnimUtils.bounceClick(card, () -> selectAnswer(answer));
    }

    private void selectAnswer(String answer) {
        viewModel.answerCurrentQuestion(answer);
        highlightSelectedOption(answer);
    }

    private void highlightSelectedOption(String answer) {
        resetOptionStyles();
        if (answer == null)
            return;

        int activeStroke = ContextCompat.getColor(requireContext(), R.color.primary);
        int activeBackground = ContextCompat.getColor(requireContext(), R.color.surface_card_selected);
        int badgeSelectedBg = ContextCompat.getColor(requireContext(), R.color.primary);
        int badgeSelectedText = ContextCompat.getColor(requireContext(), R.color.on_primary);

        MaterialCardView selected = null;
        TextView badge = null;

        switch (answer) {
            case "A":
                selected = binding.cardOptionA;
                badge = binding.badgeOptionA;
                break;
            case "B":
                selected = binding.cardOptionB;
                badge = binding.badgeOptionB;
                break;
            case "C":
                selected = binding.cardOptionC;
                badge = binding.badgeOptionC;
                break;
            case "D":
                selected = binding.cardOptionD;
                badge = binding.badgeOptionD;
                break;
            case "E":
                selected = binding.cardOptionE;
                badge = binding.badgeOptionE;
                break;
        }

        if (selected != null && badge != null) {
            selected.setStrokeColor(activeStroke);
            selected.setStrokeWidth(dpToPx(2f));
            selected.setCardBackgroundColor(activeBackground);
            badge.setBackgroundTintList(ColorStateList.valueOf(badgeSelectedBg));
            badge.setTextColor(badgeSelectedText);
        }
    }

    private void resetOptionStyles() {
        int defaultStroke = ContextCompat.getColor(requireContext(), R.color.outline);
        int defaultBg = ContextCompat.getColor(requireContext(), R.color.surface);
        int defaultBadgeText = ContextCompat.getColor(requireContext(), R.color.primary);

        MaterialCardView[] cards = { binding.cardOptionA, binding.cardOptionB, binding.cardOptionC,
                binding.cardOptionD, binding.cardOptionE };
        TextView[] badges = { binding.badgeOptionA, binding.badgeOptionB, binding.badgeOptionC,
                binding.badgeOptionD, binding.badgeOptionE };
        TextView[] texts = { binding.tvOptionA, binding.tvOptionB, binding.tvOptionC,
                binding.tvOptionD, binding.tvOptionE };

        for (int i = 0; i < cards.length; i++) {
            cards[i].setStrokeColor(defaultStroke);
            cards[i].setStrokeWidth(dpToPx(1.2f));
            cards[i].setCardBackgroundColor(defaultBg);
            cards[i].setEnabled(true);
            cards[i].setAlpha(1.0f);

            badges[i].setBackgroundTintList(null);
            badges[i].setTextColor(defaultBadgeText);

            texts[i].setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface));
            texts[i].setPaintFlags(texts[i].getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    private void showJokerDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.action_joker_fifty)
                .setMessage(R.string.dialog_joker_reward_message)
                .setPositiveButton(R.string.action_watch_ad, (dialog, which) -> {
                    AdManager.showRewardedAd(requireActivity(), () -> {
                        viewModel.applyFiftyFiftyJoker();
                    });
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    private void showExitConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_exit_test_title)
                .setMessage(R.string.dialog_finish_test_message)
                .setPositiveButton(R.string.action_finish, (dialog, which) -> viewModel.submitExam())
                .setNegativeButton(R.string.action_continue, null)
                .show();
    }

    private void showSubmitConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.action_finish_mock_exam)
                .setMessage(R.string.dialog_finish_test_message)
                .setPositiveButton(R.string.action_finish, (dialog, which) -> viewModel.submitExam())
                .setNegativeButton(R.string.action_continue, null)
                .show();
    }

    @Override
    protected void observeData() {
        viewModel.getExamTitleLiveData().observe(getViewLifecycleOwner(), title -> {
            if (title != null && !title.isEmpty()) {
                binding.tvExamTitle.setText(title);
            }
        });

        viewModel.getCurrentQuestion().observe(getViewLifecycleOwner(), question -> {
            if (question != null) {
                animateQuestionTransition(question);
            }
        });

        viewModel.getCurrentQuestionIndex().observe(getViewLifecycleOwner(), index -> {
            if (index != null) {
                int total = viewModel.getTotalQuestions();
                binding.tvQuestionNumber.setText(getString(R.string.question_counter_format, index + 1, total));
                binding.progressBar.setProgress((index + 1) * 100 / Math.max(1, total));
                binding.btnPrevious.setEnabled(index > 0);
                binding.btnNext.setEnabled(index < total - 1);
            }
        });

        viewModel.getEliminatedOptions().observe(getViewLifecycleOwner(), eliminated -> {
            if (eliminated != null && !eliminated.isEmpty()) {
                applyEliminations(eliminated);
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

    private void animateQuestionTransition(Question question) {
        binding.cardQuestion.animate().alpha(0.3f).setDuration(80).withEndAction(() -> {
            updateQuestionUI(question);
            binding.cardQuestion.animate().alpha(1.0f).setDuration(120).start();
        }).start();
    }

    private void updateQuestionUI(Question question) {
        binding.tvQuestionText.setText(question.getText());

        // Category and Section badge
        String cat = question.getCategory() != null ? question.getCategory() : "Genel";
        String section = question.getSectionName() != null ? question.getSectionName() : "Genel";
        binding.tvSectionBadge.setText(String.format("%s • %s", section.toUpperCase(), cat.toUpperCase()));

        binding.tvCategoryChip.setText(cat);

        binding.tvOptionA.setText(question.getOptionA());
        binding.tvOptionB.setText(question.getOptionB());
        binding.tvOptionC.setText(question.getOptionC());
        binding.tvOptionD.setText(question.getOptionD());
        if (question.hasOptionE()) {
            binding.cardOptionE.setVisibility(View.VISIBLE);
            binding.tvOptionE.setText(question.getOptionE());
        } else {
            binding.cardOptionE.setVisibility(View.GONE);
        }

        boolean flagged = question.isFlagged();
        binding.btnFlagQuestion.setText(flagged ? R.string.mock_flagged_question : R.string.mock_flag_question);
        binding.btnFlagQuestion.setTextColor(ContextCompat.getColor(requireContext(),
                flagged ? R.color.warning : R.color.on_surface_variant));

        resetOptionStyles();
        if (question.getUserAnswer() != null) {
            highlightSelectedOption(question.getUserAnswer());
        }
    }

    /** Interactive Question Navigator Grid: jump to any question & see answered / flagged / blank status. */
    private void showQuestionPaletteDialog() {
        List<Question> all = viewModel.getAllQuestions();
        if (all == null || all.isEmpty() || getContext() == null) return;
        int answered = 0, flagged = 0;
        for (Question q : all) {
            if (q.isAnswered()) answered++;
            if (q.isFlagged()) flagged++;
        }
        int blank = all.size() - answered;

        float dp = getResources().getDisplayMetrics().density;
        android.widget.ScrollView scroll = new android.widget.ScrollView(requireContext());
        android.widget.GridLayout grid = new android.widget.GridLayout(requireContext());
        grid.setColumnCount(5);
        int pad = (int) (14 * dp);
        grid.setPadding(pad, pad, pad, pad);
        scroll.addView(grid);

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.mock_palette_title_fmt, answered, blank, flagged))
                .setView(scroll)
                .setNegativeButton(R.string.action_close, null)
                .create();

        Integer curIdx = viewModel.getCurrentQuestionIndex().getValue();
        int current = curIdx != null ? curIdx : 0;
        int cellSize = (int) (46 * dp);
        int margin = (int) (5 * dp);

        for (int i = 0; i < all.size(); i++) {
            final int qIdx = i;
            Question q = all.get(i);
            TextView tv = new TextView(requireContext());
            tv.setGravity(android.view.Gravity.CENTER);
            tv.setTextSize(13f);
            String label = String.valueOf(i + 1) + (q.isFlagged() ? "🚩" : "");
            tv.setText(label);

            android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
            bg.setCornerRadius(10 * dp);
            if (i == current) {
                bg.setColor(ContextCompat.getColor(requireContext(), R.color.primary));
                tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_primary));
            } else if (q.isFlagged()) {
                bg.setColor(0x33F59E0B);
                bg.setStroke((int) dp, 0xFFF59E0B);
                tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface));
            } else if (q.isAnswered()) {
                bg.setColor(0x2E10B981);
                bg.setStroke((int) dp, 0xFF10B981);
                tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface));
            } else {
                bg.setColor(ContextCompat.getColor(requireContext(), R.color.surface));
                bg.setStroke((int) dp, ContextCompat.getColor(requireContext(), R.color.outline));
                tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));
            }
            tv.setBackground(bg);

            android.widget.GridLayout.LayoutParams lp = new android.widget.GridLayout.LayoutParams();
            lp.width = cellSize;
            lp.height = cellSize;
            lp.setMargins(margin, margin, margin, margin);
            tv.setOnClickListener(v -> {
                viewModel.goToQuestion(qIdx);
                dialog.dismiss();
            });
            grid.addView(tv, lp);
        }
        dialog.show();
    }

    private void applyEliminations(List<String> eliminated) {
        for (String opt : eliminated) {
            MaterialCardView card = null;
            TextView text = null;
            if ("A".equalsIgnoreCase(opt)) {
                card = binding.cardOptionA;
                text = binding.tvOptionA;
            } else if ("B".equalsIgnoreCase(opt)) {
                card = binding.cardOptionB;
                text = binding.tvOptionB;
            } else if ("C".equalsIgnoreCase(opt)) {
                card = binding.cardOptionC;
                text = binding.tvOptionC;
            } else if ("D".equalsIgnoreCase(opt)) {
                card = binding.cardOptionD;
                text = binding.tvOptionD;
            }
            if (card != null && text != null) {
                card.setEnabled(false);
                card.setAlpha(0.35f);
                text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
    }



    private void updateTimer(Long timeMs) {
        if (timeMs == null)
            return;
        int totalSeconds = (int) (timeMs / 1000);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        binding.tvTimer.setText(getString(R.string.timer_format_mm_ss, minutes, seconds));
        if (totalSeconds <= 300) {
            binding.tvTimer.setTextColor(ContextCompat.getColor(requireContext(), R.color.error));
        } else {
            binding.tvTimer.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
        }
    }

    private int dpToPx(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }
}
