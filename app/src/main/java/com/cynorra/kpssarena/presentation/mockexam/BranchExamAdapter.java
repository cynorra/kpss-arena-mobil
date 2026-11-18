package com.cynorra.kpssarena.presentation.mockexam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.ItemBranchExamBinding;
import com.cynorra.kpssarena.domain.model.BranchExam;
import com.cynorra.kpssarena.domain.model.ScoreResult;
import com.cynorra.kpssarena.presentation.common.AnimUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BranchExamAdapter extends RecyclerView.Adapter<BranchExamAdapter.ViewHolder> {

    public interface OnBranchExamClickListener {
        void onBranchExamClick(BranchExam exam);
    }

    private final List<BranchExam> items = new ArrayList<>();
    private final OnBranchExamClickListener listener;

    public BranchExamAdapter(OnBranchExamClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<BranchExam> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBranchExamBinding binding = ItemBranchExamBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemBranchExamBinding binding;

        ViewHolder(ItemBranchExamBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            AnimUtils.scalePressEffect(binding.cardBranchExam);
            AnimUtils.scalePressEffect(binding.btnAction);
        }

        void bind(BranchExam exam, OnBranchExamClickListener listener) {
            Context ctx = binding.getRoot().getContext();

            binding.tvTestNumberBadge.setText(ctx.getString(R.string.exam_badge_number, exam.getTestNumber()));
            binding.tvBranchExamTitle.setText(exam.getTitle());
            binding.tvBranchExamMeta.setText(ctx.getString(R.string.exam_branch_test_meta_format,
                    exam.getTotalQuestions(), exam.getDurationMinutes()));

            ScoreResult lastResult = exam.getLastResult();
            if (lastResult != null) {
                binding.tvBranchExamStatus.setText(String.format(Locale.getDefault(), "✓ %.2f Net", (double) lastResult.getNetScore()));
                binding.tvBranchExamStatus.setBackgroundResource(R.drawable.bg_badge_correct);
                binding.tvBranchExamStatus.setTextColor(ContextCompat.getColor(ctx, R.color.success));
                binding.btnAction.setText(R.string.exam_action_retake);
            } else {
                binding.tvBranchExamStatus.setText(R.string.exam_status_unsolved);
                binding.tvBranchExamStatus.setBackgroundResource(R.drawable.bg_badge_empty);
                binding.tvBranchExamStatus.setTextColor(ContextCompat.getColor(ctx, R.color.on_surface_variant));
                binding.btnAction.setText(R.string.exam_action_start);
            }

            binding.cardBranchExam.setOnClickListener(v -> {
                if (listener != null) listener.onBranchExamClick(exam);
            });

            binding.btnAction.setOnClickListener(v -> {
                if (listener != null) listener.onBranchExamClick(exam);
            });
        }
    }
}
