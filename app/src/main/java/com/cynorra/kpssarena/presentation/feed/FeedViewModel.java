package com.cynorra.kpssarena.presentation.feed;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.domain.model.FeedPost;
import com.cynorra.kpssarena.domain.repository.FeedRepository;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
@HiltViewModel
public class FeedViewModel extends BaseViewModel {
    private static final int FEED_LIMIT = 50;
    private final FeedRepository feedRepository;
    private final MutableLiveData<List<FeedPost>> feedPosts = new MutableLiveData<>();
    @Inject
    public FeedViewModel(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
        loadFeed();
    }
    public void loadFeed() {
        setLoading(true);
        feedRepository.getFeed(FEED_LIMIT)
                .addOnSuccessListener(posts -> {
                    setLoading(false);
                    feedPosts.setValue(posts != null ? posts : new ArrayList<>());
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    feedPosts.setValue(new ArrayList<>());
                });
    }
    public void toggleLike(FeedPost tapped) {
        // Work from the latest state of the post (rapid taps), not from the possibly stale tapped instance
        FeedPost latest = findPost(tapped.getPostId());
        if (latest == null) return;
        boolean wasLiked = latest.isLikedByMe();
        // Optimistic update with a NEW object: mutating the shared instance in place made DiffUtil think
        // nothing changed, so the heart never updated on screen.
        replacePost(latest.getPostId(), !wasLiked);
        feedRepository.toggleLike(latest.getPostId(), wasLiked)
                .addOnFailureListener(e -> {
                    // Roll back on failure
                    replacePost(latest.getPostId(), wasLiked);
                });
    }
    private FeedPost findPost(String postId) {
        List<FeedPost> current = feedPosts.getValue();
        if (current == null || postId == null) return null;
        for (FeedPost p : current) {
            if (postId.equals(p.getPostId())) return p;
        }
        return null;
    }
    /** Sets the liked state and moves the like count by +1 / -1 relative to the previous state. */
    private void replacePost(String postId, boolean liked) {
        List<FeedPost> current = feedPosts.getValue();
        if (current == null) return;
        List<FeedPost> updated = new ArrayList<>(current.size());
        for (FeedPost p : current) {
            if (postId != null && postId.equals(p.getPostId()) && p.isLikedByMe() != liked) {
                FeedPost copy = p.copy();
                copy.setLikedByMe(liked);
                copy.setLikeCount(Math.max(0, p.getLikeCount() + (liked ? 1 : -1)));
                updated.add(copy);
            } else {
                updated.add(p);
            }
        }
        feedPosts.setValue(updated);
    }
    public LiveData<List<FeedPost>> getFeedPosts() { return feedPosts; }
}
