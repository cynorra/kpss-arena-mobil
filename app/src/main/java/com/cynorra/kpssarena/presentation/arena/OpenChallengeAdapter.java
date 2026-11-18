package com.cynorra.kpssarena.presentation.arena;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.ItemOpenChallengeBinding;
import com.cynorra.kpssarena.domain.model.Match;

public class OpenChallengeAdapter extends ListAdapter<Match, OpenChallengeAdapter.ViewHolder> {

    public interface OnChallengeClickListener {
        void onChallengeClick(Match match);
    }

    private final OnChallengeClickListener listener;

    public OpenChallengeAdapter(OnChallengeClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOpenChallengeBinding binding = ItemOpenChallengeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemOpenChallengeBinding binding;

        ViewHolder(ItemOpenChallengeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Match match, OnChallengeClickListener listener) {
            Context ctx = binding.getRoot().getContext();
            boolean isAsync = "ASYNC_EXAM".equals(match.getMatchType());

            binding.tvChallengeBadge.setText(isAsync
                    ? (match.getExamTitle() != null ? match.getExamTitle() : ctx.getString(R.string.match_exam_duel_badge))
                    : ctx.getString(R.string.match_live_badge));

            String creator = match.getPlayer1Username() != null ? match.getPlayer1Username() : "Aday";
            binding.tvChallengeCreator.setText(creator);
            binding.tvChallengeTitle.setText(match.getDisplayTitle(ctx));

            if (isAsync) {
                binding.tvChallengeSubtitle.setText(ctx.getString(R.string.action_create_exam_duel_desc));
            } else {
                binding.tvChallengeSubtitle.setText(ctx.getString(R.string.match_waiting_subtitle_personalized));
            }

            binding.btnAccept.setOnClickListener(v -> listener.onChallengeClick(match));
            binding.getRoot().setOnClickListener(v -> listener.onChallengeClick(match));
        }
    }

    private static final DiffUtil.ItemCallback<Match> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Match>() {
                @Override
                public boolean areItemsTheSame(@NonNull Match oldItem, @NonNull Match newItem) {
                    return oldItem.getMatchId().equals(newItem.getMatchId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Match oldItem, @NonNull Match newItem) {
                    return oldItem.getStatus().equals(newItem.getStatus())
                            && (oldItem.getPlayer2Uid() == null ? "" : oldItem.getPlayer2Uid())
                            .equals(newItem.getPlayer2Uid() == null ? "" : newItem.getPlayer2Uid());
                }
            };
}
