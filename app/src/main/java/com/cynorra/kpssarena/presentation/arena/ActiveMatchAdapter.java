package com.cynorra.kpssarena.presentation.arena;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.ItemActiveMatchBinding;
import com.cynorra.kpssarena.domain.model.Match;
import com.google.firebase.auth.FirebaseAuth;
public class ActiveMatchAdapter extends ListAdapter<Match, ActiveMatchAdapter.ViewHolder> {
    public interface OnMatchClickListener {
        void onMatchClick(Match match);
    }
    private final OnMatchClickListener listener;
    public ActiveMatchAdapter(OnMatchClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemActiveMatchBinding binding = ItemActiveMatchBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemActiveMatchBinding binding;
        ViewHolder(ItemActiveMatchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(Match match, OnMatchClickListener listener) {
            String myUid = FirebaseAuth.getInstance().getCurrentUser() != null
                    ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
            String opponentUsername = match.getOpponentUsername(myUid);
            binding.tvOpponent.setText(opponentUsername != null ? opponentUsername
                    : binding.getRoot().getContext().getString(R.string.match_opponent_pending));
            boolean resumable = match.getStatus() == Match.MatchStatus.WAITING;
            binding.tvStatus.setText(statusLabel(match.getStatus()));
            binding.tvArrow.setVisibility(resumable ? View.VISIBLE : View.GONE);
            binding.getRoot().setClickable(resumable);
            binding.getRoot().setOnClickListener(resumable ? v -> listener.onMatchClick(match) : null);
        }
        private int statusLabel(Match.MatchStatus status) {
            switch (status) {
                case WAITING: return R.string.match_status_waiting;
                case READY: return R.string.match_status_ready;
                case IN_PROGRESS: return R.string.match_status_in_progress;
                default: return R.string.match_status_waiting;
            }
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
                    return oldItem.getStatus().equals(newItem.getStatus());
                }
            };
}
