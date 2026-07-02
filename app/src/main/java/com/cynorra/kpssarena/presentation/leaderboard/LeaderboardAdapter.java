package com.cynorra.kpssarena.presentation.leaderboard;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.ItemLeaderboardBinding;
import com.cynorra.kpssarena.domain.model.LeaderboardEntry;
public class LeaderboardAdapter extends ListAdapter<LeaderboardEntry, LeaderboardA
    private final boolean isMini;
    private OnItemClickListener onItemClickListener;
    public LeaderboardAdapter(boolean isMini) {
        super(DIFF_CALLBACK);
        this.isMini = isMini;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLeaderboardBinding binding = ItemLeaderboardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaderboardEntry entry = getItem(position);

        holder.bind(entry, isMini);
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(entry);
            }
        });
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    public interface OnItemClickListener {
        void onClick(LeaderboardEntry entry);
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemLeaderboardBinding binding;
        ViewHolder(ItemLeaderboardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(LeaderboardEntry entry, boolean isMini) {
            binding.tvRank.setText(entry.getRankBadge());
            binding.tvUsername.setText(entry.getUsername());
            binding.tvScore.setText(String.valueOf(entry.getTotalScore()));
            if (entry.getPhotoUrl() != null && !entry.getPhotoUrl().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(entry.getPhotoUrl())
                        .circleCrop()
                        .into(binding.ivAvatar);
            }
            // Current user highlight
            if (entry.isCurrentUser()) {
                binding.getRoot().setBackgroundColor(

                        binding.getRoot().getContext().getResources()
                                .getColor(R.color.primary_container, null));
            } else {
                binding.getRoot().setBackgroundColor(
                        binding.getRoot().getContext().getResources()
                                .getColor(android.R.color.transparent, null));
            }
            // Top 3 styling
            if (entry.getRank() <= 3) {
                int colorRes = entry.getRank() == 1 ? R.color.gold_rank :
                               entry.getRank() == 2 ? R.color.silver_rank :
                               R.color.bronze_rank;
                binding.tvRank.setTextColor(
                        binding.getRoot().getContext().getResources().getColor(colorRes, null));
            }
        }
    }
    private static final DiffUtil.ItemCallback<LeaderboardEntry> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<LeaderboardEntry>() {
                @Override
                public boolean areItemsTheSame(@NonNull LeaderboardEntry oldItem,
                                               @NonNull LeaderboardEntry newItem) {
                    return oldItem.getUid().equals(newItem.getUid());
                }
                @Override
                public boolean areContentsTheSame(@NonNull LeaderboardEntry oldItem,
                                                  @NonNull LeaderboardEntry newItem) {
                    return oldItem.getRank() == newItem.getRank() &&
                           oldItem.getTotalScore() == newItem.getTotalScore();
                }
            };
}
