package com.cynorra.kpssarena.presentation.flashcard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentFlashcardBinding;
import com.cynorra.kpssarena.domain.model.Question;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import com.cynorra.kpssarena.presentation.common.AnimUtils;
import com.google.android.material.chip.Chip;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FlashcardFragment extends BaseFragment<FragmentFlashcardBinding, FlashcardViewModel> {

    @Override
    protected FragmentFlashcardBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentFlashcardBinding.inflate(inflater, container, false);
    }

    @Override
    protected Class<FlashcardViewModel> getViewModelClass() {
        return FlashcardViewModel.class;
    }

    @Override
    protected void initViews() {
        String[] categories = getResources().getStringArray(R.array.mistake_category_filters);
        for (int i = 0; i < categories.length; i++) {
            final int idx = i;
            final String label = categories[i];
            Chip chip = new Chip(requireContext());
            chip.setText(label);
            chip.setCheckable(true);
            chip.setChecked(i == 0);
            chip.setOnClickListener(v -> viewModel.loadCards(idx == 0 ? null : label));
            binding.chipGroupCategory.addView(chip);
        }

        View.OnClickListener flipAction = v -> {
            if (!Boolean.TRUE.equals(viewModel.getAnswerShown().getValue())) {
                animate3DFlip(() -> viewModel.showAnswer());
            }
        };
        binding.btnShowAnswer.setOnClickListener(flipAction);
        binding.cardFlashcard.setOnClickListener(flipAction);
        binding.btnKnownCard.setOnClickListener(v -> viewModel.markKnown());
        binding.btnRepeatCard.setOnClickListener(v -> viewModel.markRepeat());

        AnimUtils.scalePressEffect(binding.btnShowAnswer);
        AnimUtils.scalePressEffect(binding.btnKnownCard);
        AnimUtils.scalePressEffect(binding.btnRepeatCard);
    }

    private void animate3DFlip(Runnable midFlip) {
        binding.cardFlashcard.setCameraDistance(8000f * getResources().getDisplayMetrics().density);
        binding.cardFlashcard.animate()
                .rotationY(90f)
                .setDuration(140)
                .withEndAction(() -> {
                    midFlip.run();
                    binding.cardFlashcard.setRotationY(-90f);
                    binding.cardFlashcard.animate().rotationY(0f).setDuration(160).start();
                })
                .start();
    }

    @Override
    protected void observeData() {
        viewModel.getCurrentCard().observe(getViewLifecycleOwner(), this::displayCard);
        viewModel.getCurrentIndex().observe(getViewLifecycleOwner(), index -> updateCounter());
        viewModel.getMasteredCount().observe(getViewLifecycleOwner(), count -> {
            int c = count != null ? count : 0;
            binding.tvMasteredBadge.setText(getString(R.string.flashcard_mastered_fmt, c));
        });
        viewModel.getAnswerShown().observe(getViewLifecycleOwner(), shown -> {
            boolean isShown = Boolean.TRUE.equals(shown);
            int vis = isShown ? View.VISIBLE : View.GONE;
            binding.dividerAnswer.setVisibility(vis);
            binding.tvAnswerText.setVisibility(vis);
            binding.tvExplanationText.setVisibility(vis);
            binding.btnShowAnswer.setVisibility(isShown ? View.GONE : View.VISIBLE);
            binding.layoutSrActions.setVisibility(vis);
        });
    }

    private void displayCard(Question question) {
        if (question == null) {
            binding.tvQuestionText.setText(R.string.flashcard_empty_state);
            binding.tvCategoryChip.setText("");
            binding.tvAnswerText.setText("");
            binding.tvExplanationText.setText("");
            return;
        }
        binding.tvCategoryChip.setText(question.getCategory());
        binding.tvQuestionText.setText(question.getText());
        binding.tvAnswerText.setText(getString(R.string.label_correct_answer_prefix) + correctOptionText(question));
        binding.tvExplanationText.setText(question.getExplanation());
        updateCounter();
    }

    private String correctOptionText(Question question) {
        String correct = question.getCorrectAnswer();
        if (correct == null) return "";
        switch (correct) {
            case "A": return question.getOptionA();
            case "B": return question.getOptionB();
            case "C": return question.getOptionC();
            case "D": return question.getOptionD();
            case "E": return question.getOptionE() != null ? question.getOptionE() : "";
            default: return "";
        }
    }

    private void updateCounter() {
        Integer index = viewModel.getCurrentIndex().getValue();
        int total = viewModel.getTotalCards();
        int current = total == 0 ? 0 : (index != null ? index : 0) + 1;
        binding.tvCounter.setText(getString(R.string.flashcard_counter_format, current, total));
    }
}
