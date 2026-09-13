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
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        UserDto dto = task.getResult().toObject(UserDto.class);
                        User user = mapDtoToUser(uid, dto);
                        cacheUserLocally(user);
                        return user;
                    }
                    return null;
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
