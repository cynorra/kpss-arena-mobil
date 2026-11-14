package com.cynorra.kpssarena.presentation.practice;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.core.content.ContextCompat;

import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentPracticeBinding;
import com.cynorra.kpssarena.domain.model.Question;
import com.cynorra.kpssarena.engagement.InviteHelper;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
import com.cynorra.kpssarena.presentation.common.ShareCardGenerator;
import com.cynorra.kpssarena.presentation.common.ShareHelper;
import com.google.android.material.button.MaterialButton;

import dagger.hilt.android.AndroidEntryPoint;

/** Topic practice: instant right/wrong feedback, explanation, XP and a shareable question card. */
@AndroidEntryPoint
public class PracticeFragment extends BaseFragment<FragmentPracticeBinding, PracticeViewModel> {
    private boolean spinnerReady = false;

    @Override
    protected FragmentPracticeBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentPracticeBinding.inflate(inflater, container, false);
    }

    @Override
    protected Class<PracticeViewModel> getViewModelClass() {
        return PracticeViewModel.class;
    }

    private MaterialButton[] optionButtons() {
        return new MaterialButton[]{binding.btnOptionA, binding.btnOptionB, binding.btnOptionC, binding.btnOptionD};
    }

    private static final String[] LETTERS = {"A", "B", "C", "D"};

    @Override
    protected void initViews() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.mistake_category_filters));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(adapter);
        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!spinnerReady) { // first callback fires on attach; the ViewModel already loaded "all"
                    spinnerReady = true;
                    return;
                }
                viewModel.loadSet(position == 0 ? null : parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        MaterialButton[] buttons = optionButtons();
        for (int i = 0; i < buttons.length; i++) {
            final String letter = LETTERS[i];
            buttons[i].setOnClickListener(v -> viewModel.answer(letter));
        }
        binding.btnNext.setOnClickListener(v -> viewModel.next());
        binding.btnShareQuestion.setOnClickListener(v -> shareCurrentQuestion());
        binding.btnShareResult.setOnClickListener(v -> shareResult());
        binding.btnAgain.setOnClickListener(v -> viewModel.newSet());
    }

    @Override
    protected void observeData() {
        viewModel.getCurrentQuestion().observe(getViewLifecycleOwner(), this::showQuestion);
        viewModel.getPosition().observe(getViewLifecycleOwner(), pos -> updateCounter());
        viewModel.getAnswerResult().observe(getViewLifecycleOwner(), this::showAnswerResult);
        viewModel.getFinished().observe(getViewLifecycleOwner(), done -> showSummary(Boolean.TRUE.equals(done)));
        viewModel.getEmpty().observe(getViewLifecycleOwner(), empty ->
                binding.tvEmpty.setVisibility(Boolean.TRUE.equals(empty) ? View.VISIBLE : View.GONE));
    }

    private void updateCounter() {
        Integer pos = viewModel.getPosition().getValue();
        int total = viewModel.getTotal();
        int current = total == 0 ? 0 : (pos != null ? pos : 0) + 1;
        binding.tvCounter.setText(total == 0 ? "" : getString(R.string.practice_counter, current, total));
        binding.progressBar.setProgress(total == 0 ? 0 : current * 100 / total);
    }

    private void showQuestion(Question q) {
        binding.layoutFeedback.setVisibility(View.GONE);
        binding.tvGoalReached.setVisibility(View.GONE);
        binding.btnNext.setEnabled(false);
        binding.btnNext.setText(R.string.practice_next);
        boolean has = q != null;
        binding.btnShareQuestion.setEnabled(has);
        for (MaterialButton b : optionButtons()) {
            b.setVisibility(has ? View.VISIBLE : View.GONE);
            resetOption(b);
        }
        binding.cardQuestion.setVisibility(has ? View.VISIBLE : View.GONE);
        if (!has) return;
        binding.tvCategoryChip.setText(q.getCategory());
        binding.tvQuestionText.setText(q.getText());
        String[] options = optionsOf(q);
        for (int i = 0; i < 4; i++) {
            optionButtons()[i].setText(LETTERS[i] + ") " + options[i]);
        }
        updateCounter();
    }

    private void resetOption(MaterialButton b) {
        b.setEnabled(true);
        b.setStrokeColorResource(R.color.primary);
        b.setStrokeWidth(2);
        b.setBackgroundColor(0x00000000);
    }

    private void showAnswerResult(PracticeViewModel.AnswerResult r) {
        if (r == null) return;
        MaterialButton[] buttons = optionButtons();
        for (int i = 0; i < buttons.length; i++) {
            MaterialButton b = buttons[i];
            b.setEnabled(false); // locks the answer; disabled look is overridden below for the two that matter
            String letter = LETTERS[i];
            if (letter.equals(r.correct)) {
                colorOption(b, R.color.success, 0x334CAF50);
            } else if (letter.equals(r.selected)) {
                colorOption(b, R.color.error, 0x33CF6679);
            }
        }
        binding.layoutFeedback.setVisibility(View.VISIBLE);
        Question q = viewModel.getCurrentQuestion().getValue();
        if (r.isCorrect) {
            binding.tvFeedback.setText(R.string.practice_correct);
            binding.tvFeedback.setTextColor(ContextCompat.getColor(requireContext(), R.color.success));
        } else {
            binding.tvFeedback.setText(getString(R.string.practice_wrong, r.correct));
            binding.tvFeedback.setTextColor(ContextCompat.getColor(requireContext(), R.color.error));
        }
        binding.tvXp.setText(getString(R.string.practice_xp_earned, r.xpEarned));
        String explanation = q != null ? q.getExplanation() : null;
        if (explanation != null && !explanation.isEmpty()) {
            binding.tvExplanation.setText(getString(R.string.practice_explanation_label) + ": " + explanation);
            binding.tvExplanation.setVisibility(View.VISIBLE);
        } else {
            binding.tvExplanation.setVisibility(View.GONE);
        }
        binding.tvGoalReached.setVisibility(r.goalJustReached ? View.VISIBLE : View.GONE);
        binding.btnNext.setEnabled(true);
        binding.btnNext.setText(viewModel.isLastQuestion() ? R.string.practice_see_result : R.string.practice_next);
        binding.scrollQuestion.post(() -> binding.scrollQuestion.fullScroll(View.FOCUS_DOWN));
    }

    private void colorOption(MaterialButton b, int strokeColorRes, int fill) {
        b.setStrokeColorResource(strokeColorRes);
        b.setStrokeWidth(5);
        b.setBackgroundColor(fill);
        b.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface));
    }

    private void showSummary(boolean done) {
        binding.layoutSummary.setVisibility(done ? View.VISIBLE : View.GONE);
        binding.layoutActions.setVisibility(done ? View.GONE : View.VISIBLE);
        if (done) {
            binding.tvSummary.setText(getString(R.string.practice_summary_text,
                    viewModel.getCorrectCount(), viewModel.getTotal(), viewModel.getXpTotal()));
        }
    }

    private String[] optionsOf(Question q) {
        return new String[]{q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()};
    }

    /** Question card WITHOUT the answer: friends have to open the app to find out. */
    private void shareCurrentQuestion() {
        Question q = viewModel.getCurrentQuestion().getValue();
        if (q == null) return;
        String invite = getString(R.string.invite_line, InviteHelper.myCode());
        Bitmap card = ShareCardGenerator.generateQuestionCard(requireContext(), q.getCategory(), q.getText(),
                optionsOf(q), getString(R.string.share_question_hook), getString(R.string.share_question_cta), invite);
        String text = getString(R.string.share_question_text, q.getText(), InviteHelper.inviteLink(requireContext()));
        ShareHelper.shareAsPost(this, text, card);
    }

    private void shareResult() {
        String invite = getString(R.string.invite_line, InviteHelper.myCode());
        Bitmap card = ShareCardGenerator.generateStoryCard(requireContext(),
                getString(R.string.share_practice_title),
                viewModel.getCorrectCount() + "/" + viewModel.getTotal(),
                getString(R.string.share_practice_label),
                getString(R.string.share_story_cta), invite);
        String text = getString(R.string.share_question_result_text,
                viewModel.getCorrectCount(), viewModel.getTotal(), InviteHelper.inviteLink(requireContext()));
        ShareHelper.shareAsPost(this, text, card);
    }
}
