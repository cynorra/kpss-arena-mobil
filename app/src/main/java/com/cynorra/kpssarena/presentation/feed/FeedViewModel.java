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
    public void toggleLike(FeedPost post) {
        boolean wasLiked = post.isLikedByMe();
        // Iyimser (optimistic) guncelleme: sunucu yaniti beklemeden UI'da aninda yansit
        post.setLikedByMe(!wasLiked);
        post.setLikeCount(post.getLikeCount() + (wasLiked ? -1 : 1));
        List<FeedPost> current = feedPosts.getValue();
        if (current != null) {
            feedPosts.setValue(new ArrayList<>(current));
        }
        feedRepository.toggleLike(post.getPostId(), wasLiked)
                .addOnFailureListener(e -> {
                    // Basarisiz olursa geri al
                    post.setLikedByMe(wasLiked);
                    post.setLikeCount(post.getLikeCount() + (wasLiked ? 1 : -1));
                    List<FeedPost> rollback = feedPosts.getValue();
                    if (rollback != null) {
                        feedPosts.setValue(new ArrayList<>(rollback));
                    }
                });
    }
    public LiveData<List<FeedPost>> getFeedPosts() { return feedPosts; }
}
