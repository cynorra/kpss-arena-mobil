package com.cynorra.kpssarena.presentation.feed;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.databinding.ItemFeedPostBinding;
import com.cynorra.kpssarena.domain.model.FeedPost;
import java.util.concurrent.TimeUnit;
public class FeedAdapter extends ListAdapter<FeedPost, FeedAdapter.ViewHolder> {
    private OnLikeClickListener onLikeClickListener;
    public FeedAdapter() {
        super(DIFF_CALLBACK);
    }
    public void setOnLikeClickListener(OnLikeClickListener listener) {
        this.onLikeClickListener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFeedPostBinding binding = ItemFeedPostBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeedPost post = getItem(position);
        holder.bind(post);
        holder.binding.tvLikeButton.setOnClickListener(v -> {
            if (onLikeClickListener != null) onLikeClickListener.onLikeClick(post);
        });
    }
    public interface OnLikeClickListener {
        void onLikeClick(FeedPost post);
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemFeedPostBinding binding;
        ViewHolder(ItemFeedPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(FeedPost post) {
            Context context = binding.getRoot().getContext();
            binding.tvUsername.setText(post.getUsername());
            binding.tvPostText.setText(buildPostText(context, post));
            binding.tvTimeAgo.setText(formatTimeAgo(context, post.getCreatedAt()));
            binding.tvLikeButton.setText(context.getString(
                    post.isLikedByMe() ? R.string.feed_like_format_liked : R.string.feed_like_format_unliked,
                    post.getLikeCount()));
            if (post.getPhotoUrl() != null && !post.getPhotoUrl().isEmpty()) {
                Glide.with(context).load(post.getPhotoUrl()).circleCrop().into(binding.ivAvatar);
            }
        }
        private String buildPostText(Context context, FeedPost post) {
            String type = post.getType();
            if (FeedPost.TYPE_MATCH_WIN.equals(type)) {
                return context.getString(R.string.feed_match_win_format, post.getUsername(), post.getOpponentUsername());
            } else if (FeedPost.TYPE_STREAK_MILESTONE.equals(type)) {
                return context.getString(R.string.feed_streak_milestone_format, post.getUsername(), post.getSecondaryValue());
            }
            return context.getString(R.string.feed_test_result_format, post.getUsername(), post.getScore());
        }
        private String formatTimeAgo(Context context, long createdAt) {
            long diffMs = System.currentTimeMillis() - createdAt;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diffMs);
            if (minutes < 1) return context.getString(R.string.feed_time_just_now);
            if (minutes < 60) return context.getString(R.string.feed_time_minutes_ago, minutes);
            long hours = TimeUnit.MILLISECONDS.toHours(diffMs);
            if (hours < 24) return context.getString(R.string.feed_time_hours_ago, hours);
            long days = TimeUnit.MILLISECONDS.toDays(diffMs);
            return context.getString(R.string.feed_time_days_ago, days);
        }
    }
    private static final DiffUtil.ItemCallback<FeedPost> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<FeedPost>() {
                @Override
                public boolean areItemsTheSame(@NonNull FeedPost oldItem, @NonNull FeedPost newItem) {
                    return oldItem.getPostId().equals(newItem.getPostId());
                }
                @Override
                public boolean areContentsTheSame(@NonNull FeedPost oldItem, @NonNull FeedPost newItem) {
                    return oldItem.getLikeCount() == newItem.getLikeCount()
                            && oldItem.isLikedByMe() == newItem.isLikedByMe();
                }
            };
}
