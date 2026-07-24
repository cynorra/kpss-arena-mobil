package com.cynorra.kpssarena.domain.repository;
import com.cynorra.kpssarena.domain.model.User;
import com.google.android.gms.tasks.Task;
public interface ProfileRepository {
    Task<User> getProfile(String uid);
    Task<Void> toggleFollow(String currentUid, String targetUserId);
}
