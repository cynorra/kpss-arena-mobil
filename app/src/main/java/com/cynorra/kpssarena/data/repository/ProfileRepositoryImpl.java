package com.cynorra.kpssarena.data.repository;
import android.content.Context;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.data.local.dao.UserCacheDao;
import com.cynorra.kpssarena.data.local.entity.UserCacheEntity;
import com.cynorra.kpssarena.data.remote.FirebaseFirestoreSource;
import com.cynorra.kpssarena.data.remote.model.UserDto;
import com.cynorra.kpssarena.domain.model.User;
import com.cynorra.kpssarena.domain.repository.ProfileRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import javax.inject.Inject;
import dagger.hilt.android.qualifiers.ApplicationContext;
public class ProfileRepositoryImpl implements ProfileRepository {
    private final FirebaseFirestoreSource firestoreSource;
    private final UserCacheDao userCacheDao;
    private final Context context;
    @Inject
    public ProfileRepositoryImpl(FirebaseFirestoreSource firestoreSource, UserCacheDao userCacheDao,
                                  @ApplicationContext Context context) {
        this.firestoreSource = firestoreSource;
        this.userCacheDao = userCacheDao;
        this.context = context;
    }
    @Override
    public Task<User> getProfile(String uid) {
        return firestoreSource.getUserProfile(uid)
                .continueWithTask(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        UserDto dto = task.getResult().toObject(UserDto.class);
                        User user = mapDtoToUser(uid, dto);
                        if (user == null) return Tasks.forResult((User) null);
                        // users/{uid}.totalScore is never written by the client (rules forbid it),
                        // so the real total is the sum of the user's daily scores.
                        return firestoreSource.getUserTestHistory(uid, 1000)
                                .continueWith(historyTask -> {
                                    if (historyTask.isSuccessful() && historyTask.getResult() != null) {
                                        int sum = 0;
                                        java.util.Set<String> testDays = new java.util.HashSet<>();
                                        for (com.google.firebase.firestore.DocumentSnapshot doc : historyTask.getResult().getDocuments()) {
                                            Long score = doc.getLong("totalScore");
                                            if (score != null) sum += score.intValue();
                                            String completedAt = doc.getString("completedAt");
                                            if (completedAt != null && completedAt.length() >= 10) {
                                                testDays.add(completedAt.substring(0, 10));
                                            }
                                        }
                                        user.setTotalScore(sum);
                                        user.setStreak(Math.max(user.getStreak(), StatsRepositoryImpl.currentStreakFrom(testDays)));
                                    }
                                    cacheUserLocally(user);
                                    return user;
                                });
                    }
                    return Tasks.forResult((User) null);
                });
    }
    @Override
    public Task<Void> toggleFollow(String currentUid, String targetUserId) {
        return firestoreSource.getUserProfile(currentUid)
                .continueWithTask(task -> {
                    if (!task.isSuccessful() || task.getResult() == null) {
                        throw task.getException() != null ? task.getException() : new Exception(context.getString(R.string.error_user_not_found));
                    }
                    UserDto dto = task.getResult().toObject(UserDto.class);
                    boolean isFollowing = dto != null && dto.getFollowing() != null
                            && dto.getFollowing().contains(targetUserId);
                    if (isFollowing) {
                        return firestoreSource.unfollowUser(currentUid, targetUserId);
                    } else {
                        return firestoreSource.followUser(currentUid, targetUserId);
                    }
                });
    }
    @Override
    public Task<Void> updateUsername(String uid, String newUsername) {
        return firestoreSource.searchUserByUsername(newUsername)
                .continueWithTask(searchTask -> {
                    if (searchTask.isSuccessful() && searchTask.getResult() != null) {
                        for (com.google.firebase.firestore.DocumentSnapshot doc : searchTask.getResult().getDocuments()) {
                            if (!doc.getId().equals(uid)) {
                                return Tasks.forException(new Exception(context.getString(R.string.error_username_taken)));
                            }
                        }
                    }
                    java.util.Map<String, Object> updates = new java.util.HashMap<>();
                    updates.put("username", newUsername);
                    return firestoreSource.updateUserProfile(uid, updates).continueWith(updateTask -> {
                        if (!updateTask.isSuccessful()) {
                            throw updateTask.getException() != null ? updateTask.getException() : new Exception(context.getString(R.string.error_action_failed));
                        }
                        // Matches use the auth display name, keep it in sync with the profile username
                        com.google.firebase.auth.FirebaseUser authUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
                        if (authUser != null) {
                            authUser.updateProfile(new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                    .setDisplayName(newUsername).build());
                        }
                        return null;
                    });
                });
    }
    private User mapDtoToUser(String uid, UserDto dto) {
        if (dto == null) return null;
        User user = new User();
        user.setUid(uid);
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhotoUrl(dto.getPhotoUrl());
        user.setTotalScore(dto.getTotalScore());
        user.setWeeklyScore(dto.getWeeklyScore());
        user.setMonthlyScore(dto.getMonthlyScore());
        user.setStreak(dto.getStreak());
        user.setLastTestDate(dto.getLastTestDate());
        if (dto.getFollowing() != null) user.setFollowing(dto.getFollowing());
        if (dto.getFollowers() != null) user.setFollowers(dto.getFollowers());
        user.setPremium(dto.isPremium());
        user.setCreatedAt(dto.getCreatedAt());
        return user;
    }
    private void cacheUserLocally(User user) {
        if (user == null) return;
        UserCacheEntity entity = new UserCacheEntity();
        entity.setUid(user.getUid());
        entity.setUsername(user.getUsername());
        entity.setTotalScore(user.getTotalScore());
        entity.setStreak(user.getStreak());
        entity.setPremium(user.isPremium());
        entity.setLastTestDate(user.getLastTestDate());
        userCacheDao.insertUser(entity).subscribe(() -> {}, throwable -> {});
    }
}
