package com.cynorra.kpssarena.presentation.test;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.ItemQuestionReviewBinding;
import com.cynorra.kpssarena.domain.model.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionReviewAdapter extends RecyclerView.Adapter<QuestionReviewAdapter.ViewHolder> {

    public enum FilterMode {
        ALL,
        WRONG_ONLY,
        EMPTY_ONLY,
        CORRECT_ONLY
    }

    private static class IndexedQuestion {
        final Question question;
        final int originalIndex;

        IndexedQuestion(Question question, int originalIndex) {
            this.question = question;
            this.originalIndex = originalIndex;
        }
    }

    private final List<IndexedQuestion> allQuestions = new ArrayList<>();
    private final List<IndexedQuestion> displayedQuestions = new ArrayList<>();
    private FilterMode currentFilter = FilterMode.ALL;

    public void setQuestions(List<Question> questions) {
        allQuestions.clear();
        if (questions != null) {
            for (int i = 0; i < questions.size(); i++) {
                allQuestions.add(new IndexedQuestion(questions.get(i), i));
            }
        }
        applyFilter(currentFilter);
    }

    public void applyFilter(FilterMode filter) {
        this.currentFilter = filter;
        displayedQuestions.clear();
        for (IndexedQuestion item : allQuestions) {
            Question q = item.question;
            boolean isCorrect = q.isCorrect();
            boolean isAnswered = q.isAnswered();

            switch (filter) {
                case ALL:
                    displayedQuestions.add(item);
                    break;
                case WRONG_ONLY:
                    if (isAnswered && !isCorrect) {
                        displayedQuestions.add(item);
                    }
                    break;
                case EMPTY_ONLY:
                    if (!isAnswered) {
                        displayedQuestions.add(item);
                    }
                    break;
                case CORRECT_ONLY:
                    if (isCorrect) {
                        displayedQuestions.add(item);
                    }
                    break;
            }
        }
        notifyDataSetChanged();
    }

    public int getCountForFilter(FilterMode filter) {
        int count = 0;
        for (IndexedQuestion item : allQuestions) {
            Question q = item.question;
            switch (filter) {
                case ALL:
                    count++;
                    break;
                case WRONG_ONLY:
                    if (q.isAnswered() && !q.isCorrect()) count++;
                    break;
                case EMPTY_ONLY:
                    if (!q.isAnswered()) count++;
                    break;
                case CORRECT_ONLY:
                    if (q.isCorrect()) count++;
                    break;
            }
        }
        return count;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemQuestionReviewBinding binding = ItemQuestionReviewBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(displayedQuestions.get(position));
    }

    @Override
    public int getItemCount() {
        return displayedQuestions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemQuestionReviewBinding binding;

        ViewHolder(ItemQuestionReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(IndexedQuestion indexedQuestion) {
            Context ctx = itemView.getContext();
            Question q = indexedQuestion.question;
            int qNum = indexedQuestion.originalIndex + 1;

            binding.tvQuestionNumber.setText(ctx.getString(R.string.question_item_number_format, qNum));

            // Category with icon
            String category = q.getCategory() != null ? q.getCategory() : "Genel";
            String categoryWithIcon = formatCategoryWithIcon(category);
            binding.tvCategoryChip.setText(categoryWithIcon);

            // Question prompt
            binding.tvQuestionText.setText(q.getText());

            // Status Badge
            boolean isCorrect = q.isCorrect();
            boolean isAnswered = q.isAnswered();
            if (isCorrect) {
                binding.tvStatusBadge.setText(R.string.status_badge_correct);
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_correct);
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(ctx, R.color.success));
            } else if (isAnswered) {
                binding.tvStatusBadge.setText(R.string.status_badge_wrong);
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_wrong);
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(ctx, R.color.error));
            } else {
                binding.tvStatusBadge.setText(R.string.status_badge_empty);
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_empty);
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(ctx, R.color.on_surface_variant));
            }

            // Options styling
            String userAns = q.getUserAnswer();
            String correctAns = q.getCorrectAnswer();

            bindOption(ctx, binding.layoutOptionA, binding.tvLetterA, binding.tvOptionA, binding.ivOptionAIcon,
                    "A", q.getOptionA(), userAns, correctAns);
            bindOption(ctx, binding.layoutOptionB, binding.tvLetterB, binding.tvOptionB, binding.ivOptionBIcon,
                    "B", q.getOptionB(), userAns, correctAns);
            bindOption(ctx, binding.layoutOptionC, binding.tvLetterC, binding.tvOptionC, binding.ivOptionCIcon,
                    "C", q.getOptionC(), userAns, correctAns);
            bindOption(ctx, binding.layoutOptionD, binding.tvLetterD, binding.tvOptionD, binding.ivOptionDIcon,
                    "D", q.getOptionD(), userAns, correctAns);

            // Explanation Box
            String explanation = q.getExplanation();
            if (explanation != null && !explanation.trim().isEmpty()) {
                binding.layoutExplanation.setVisibility(View.VISIBLE);
                binding.tvExplanation.setText(explanation);
            } else {
                binding.layoutExplanation.setVisibility(View.GONE);
            }
        }

        private void bindOption(Context ctx, LinearLayout layout, TextView tvLetter, TextView tvText,
                                ImageView ivIcon, String letter, String optionText,
                                String userAnswer, String correctAnswer) {
            tvText.setText(optionText != null ? optionText : "");
            boolean isThisCorrect = correctAnswer != null && correctAnswer.equalsIgnoreCase(letter);
            boolean isThisUserSelected = userAnswer != null && userAnswer.equalsIgnoreCase(letter);

            GradientDrawable background = new GradientDrawable();
            background.setCornerRadius(dpToPx(ctx, 12));

            if (isThisCorrect) {
                // Correct answer -> Vibrant Green Highlight
                background.setColor(Color.parseColor("#1B3320"));
                background.setStroke(dpToPx(ctx, 1.5f), ContextCompat.getColor(ctx, R.color.success));
                layout.setBackground(background);

                tvLetter.setBackgroundResource(R.drawable.bg_option_letter_default);
                tvLetter.setTextColor(ContextCompat.getColor(ctx, R.color.success));
                tvText.setTextColor(ContextCompat.getColor(ctx, R.color.on_surface));

                ivIcon.setVisibility(View.VISIBLE);
                ivIcon.setImageResource(R.drawable.ic_check);
                ivIcon.setColorFilter(ContextCompat.getColor(ctx, R.color.success));
            } else if (isThisUserSelected) {
                // Wrong user selection -> Red Highlight
                background.setColor(Color.parseColor("#3B1822"));
                background.setStroke(dpToPx(ctx, 1.5f), ContextCompat.getColor(ctx, R.color.error));
                layout.setBackground(background);

                tvLetter.setBackgroundResource(R.drawable.bg_option_letter_default);
                tvLetter.setTextColor(ContextCompat.getColor(ctx, R.color.error));
                tvText.setTextColor(ContextCompat.getColor(ctx, R.color.on_surface));

                ivIcon.setVisibility(View.VISIBLE);
                ivIcon.setImageResource(R.drawable.ic_close);
                ivIcon.setColorFilter(ContextCompat.getColor(ctx, R.color.error));
            } else {
                // Neutral unselected option
                background.setColor(Color.parseColor("#162035"));
                background.setStroke(dpToPx(ctx, 1f), ContextCompat.getColor(ctx, R.color.outline));
                layout.setBackground(background);

                tvLetter.setBackgroundResource(R.drawable.bg_option_letter_default);
                tvLetter.setTextColor(ContextCompat.getColor(ctx, R.color.on_surface_variant));
                tvText.setTextColor(ContextCompat.getColor(ctx, R.color.on_surface_variant));

                ivIcon.setVisibility(View.GONE);
            }
        }

        private static int dpToPx(Context context, float dp) {
            return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
        }

        private static String formatCategoryWithIcon(String category) {
            if (category == null) return "Genel";
            String cat = category.trim();
            if (cat.equalsIgnoreCase("Tarih")) return "📚 Tarih";
            if (cat.equalsIgnoreCase("Matematik")) return "📐 Matematik";
            if (cat.equalsIgnoreCase("Türkçe")) return "📝 Türkçe";
            if (cat.equalsIgnoreCase("Coğrafya")) return "🌍 Coğrafya";
            if (cat.equalsIgnoreCase("Vatandaşlık")) return "⚖️ Vatandaşlık";
            if (cat.equalsIgnoreCase("Güncel Bilgiler")) return "💡 Güncel";
            return "📌 " + cat;
        }
    }
}
