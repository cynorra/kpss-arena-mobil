package com.cynorra.kpssarena.domain.repository;
import com.cynorra.kpssarena.domain.model.FeedPost;
import com.google.android.gms.tasks.Task;
import java.util.List;
public interface FeedRepository {
    Task<List<FeedPost>> getFeed(int limit);
    Task<Void> toggleLike(String postId, boolean currentlyLiked);
}
