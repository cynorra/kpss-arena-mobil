package com.cynorra.kpssarena.data.repository;
import com.cynorra.kpssarena.data.remote.FirebaseFirestoreSource;
import com.cynorra.kpssarena.data.remote.model.FeedPostDto;
import com.cynorra.kpssarena.domain.model.FeedPost;
import com.cynorra.kpssarena.domain.repository.FeedRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
public class FeedRepositoryImpl implements FeedRepository {
    private final FirebaseFirestoreSource firestoreSource;
    @Inject
    public FeedRepositoryImpl(FirebaseFirestoreSource firestoreSource) {
        this.firestoreSource = firestoreSource;
    }
    @Override
    public Task<List<FeedPost>> getFeed(int limit) {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        return firestoreSource.getFeedPosts(limit).continueWith(task -> {
            List<FeedPost> posts = new ArrayList<>();
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    FeedPostDto dto = doc.toObject(FeedPostDto.class);
                    if (dto != null) {
                        posts.add(mapDtoToPost(doc.getId(), dto, currentUid));
                    }
                }
            }
            return posts;
        });
    }
    private FeedPost mapDtoToPost(String postId, FeedPostDto dto, String currentUid) {
        FeedPost post = new FeedPost();
        post.setPostId(postId);
        post.setUid(dto.getUid());
        post.setUsername(dto.getUsername());
        post.setPhotoUrl(dto.getPhotoUrl());
        post.setType(dto.getType());
        post.setScore(dto.getScore());
        post.setSecondaryValue(dto.getSecondaryValue());
        post.setOpponentUsername(dto.getOpponentUsername());
        post.setCreatedAt(dto.getCreatedAt());
        List<String> likedBy = dto.getLikedBy();
        post.setLikeCount(likedBy != null ? likedBy.size() : 0);
        post.setLikedByMe(likedBy != null && currentUid != null && likedBy.contains(currentUid));
        return post;
    }
    @Override
    public Task<Void> toggleLike(String postId, boolean currentlyLiked) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return currentlyLiked
                ? firestoreSource.unlikeFeedPost(postId, uid)
                : firestoreSource.likeFeedPost(postId, uid);
    }
}
