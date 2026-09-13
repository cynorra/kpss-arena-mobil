package com.cynorra.kpssarena.presentation.flashcard;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.FragmentFlashcardBinding;
import com.cynorra.kpssarena.domain.model.Question;
import com.cynorra.kpssarena.presentation.base.BaseFragment;
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
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.mistake_category_filters)
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(spinnerAdapter);
        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = position == 0 ? null : parent.getItemAtPosition(position).toString();
                viewModel.loadCards(category);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        binding.btnShowAnswer.setOnClickListener(v -> viewModel.showAnswer());
        binding.btnNextCard.setOnClickListener(v -> viewModel.nextCard());
    }
    @Override
    protected void observeData() {
        viewModel.getCurrentCard().observe(getViewLifecycleOwner(), this::displayCard);
        viewModel.getCurrentIndex().observe(getViewLifecycleOwner(), index -> updateCounter());
        viewModel.getAnswerShown().observe(getViewLifecycleOwner(), shown -> {
            int visibility = shown != null && shown ? View.VISIBLE : View.GONE;
            binding.dividerAnswer.setVisibility(visibility);
            binding.tvAnswerText.setVisibility(visibility);
            binding.tvExplanationText.setVisibility(visibility);
            binding.btnShowAnswer.setVisibility(shown != null && shown ? View.GONE : View.VISIBLE);
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
