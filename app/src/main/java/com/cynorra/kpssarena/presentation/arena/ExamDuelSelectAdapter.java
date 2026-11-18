package com.cynorra.kpssarena.presentation.arena;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.ItemExamDuelSelectBinding;
import com.cynorra.kpssarena.domain.model.ExamType;
import com.cynorra.kpssarena.domain.model.ScoreResult;

import java.util.List;
import java.util.Map;

public class ExamDuelSelectAdapter extends RecyclerView.Adapter<ExamDuelSelectAdapter.ViewHolder> {

    public interface OnExamOptionClickListener {
        void onChallengeWithScore(ExamType type, ScoreResult result);
        void onSolveExamFirst(ExamType type);
    }

    private final List<ExamType> examTypes;
    private final Map<ExamType, ScoreResult> userScores;
    private final OnExamOptionClickListener listener;

    public ExamDuelSelectAdapter(List<ExamType> examTypes,
                                 Map<ExamType, ScoreResult> userScores,
                                 OnExamOptionClickListener listener) {
        this.examTypes = examTypes;
        this.userScores = userScores;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExamDuelSelectBinding binding = ItemExamDuelSelectBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(examTypes.get(position), userScores.get(examTypes.get(position)), listener);
    }

    @Override
    public int getItemCount() {
        return examTypes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemExamDuelSelectBinding binding;

        ViewHolder(ItemExamDuelSelectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ExamType type, ScoreResult result, OnExamOptionClickListener listener) {
            Context ctx = binding.getRoot().getContext();
            binding.tvExamTitle.setText(type.getTitle());
            binding.tvExamSubtitle.setText(type.getSubtitle());

            if (result != null) {
                binding.tvStatusBadge.setText(ctx.getString(R.string.leaderboard_tab_today));
                int minutes = Math.max(1, (result.getTotalTimeUsed() / 1000) / 60);
                binding.tvScoreDesc.setText(ctx.getString(R.string.duel_score_summary,
                        (double) result.getNetScore(),
                        result.getCorrectCount(),
                        result.getWrongCount(),
                        minutes));
                binding.btnAction.setText(ctx.getString(R.string.action_challenge_with_score));
                binding.btnAction.setOnClickListener(v -> listener.onChallengeWithScore(type, result));
            } else {
                binding.tvStatusBadge.setText(ctx.getString(R.string.duel_no_result));
                binding.tvScoreDesc.setText(ctx.getString(R.string.duel_no_result));
                binding.btnAction.setText(ctx.getString(R.string.action_solve_first));
                binding.btnAction.setOnClickListener(v -> listener.onSolveExamFirst(type));
            }
        }
    }
}
