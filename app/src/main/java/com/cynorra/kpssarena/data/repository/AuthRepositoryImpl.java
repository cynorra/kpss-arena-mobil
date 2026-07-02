package com.cynorra.kpssarena.data.repository;
import com.cynorra.kpssarena.data.remote.FirebaseAuthSource;
import com.cynorra.kpssarena.data.remote.FirebaseFirestoreSource;
import com.cynorra.kpssarena.data.remote.model.UserDto;
import com.cynorra.kpssarena.domain.model.User;

import com.cynorra.kpssarena.domain.repository.AuthRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import javax.inject.Inject;
public class AuthRepositoryImpl implements AuthRepository {
    private final FirebaseAuthSource authSource;
    private final FirebaseFirestoreSource firestoreSource;
    private final FirebaseAnalytics analytics;
    @Inject
    public AuthRepositoryImpl(FirebaseAuthSource authSource,
                              FirebaseFirestoreSource firestoreSource,
                              FirebaseAnalytics analytics) {
        this.authSource = authSource;
        this.firestoreSource = firestoreSource;
        this.analytics = analytics;
    }
    @Override
    public Task<AuthResult> loginWithEmail(String email, String password) {
        return authSource.signInWithEmail(email, password)
                .addOnSuccessListener(result -> {
                    analytics.logEvent("login", null);
                });
    }
    @Override
    public Task<AuthResult> registerWithEmail(String email, String password) {
        return authSource.createUserWithEmail(email, password)
                .addOnSuccessListener(result -> {
                    analytics.logEvent("sign_up", null);
                });
    }
    @Override

    public Task<AuthResult> signInWithGoogle(String idToken) {
        return authSource.signInWithGoogle(idToken)
                .addOnSuccessListener(result -> {
                    analytics.logEvent("login_google", null);
                });
    }
    @Override
    public Task<Void> sendPasswordResetEmail(String email) {
        return authSource.sendPasswordResetEmail(email);
    }
    @Override
    public void logout() {
        authSource.signOut();
    }
    @Override
    public User getCurrentUser() {
        return authSource.getCurrentUser();
    }
    @Override
    public boolean isLoggedIn() {
        return authSource.isLoggedIn();
    }
    @Override
    public Task<Boolean> isAdmin(String uid) {
        return firestoreSource.getUserClaims(uid)
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        return Boolean.TRUE.equals(task.getResult().getBoolean("isAdmin"));
                    }
                    return false;
                });
    }
    @Override
    public Task<Void> createUserProfile(User user) {

        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhotoUrl(user.getPhotoUrl());
        dto.setCreatedAt(System.currentTimeMillis());
        return firestoreSource.createUserProfile(user.getUid(), dto);
    }
    @Override
    public Task<User> getUserProfile(String uid) {
        return firestoreSource.getUserProfile(uid)
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        UserDto dto = task.getResult().toObject(UserDto.class);
                        return mapDtoToUser(uid, dto);
                    }
                    return null;
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
        user.setPremium(dto.isPremium());
        user.setCreatedAt(dto.getCreatedAt());
        return user;
    }
}
