package com.cynorra.kpssarena.domain.repository;
import com.cynorra.kpssarena.domain.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
public interface AuthRepository {
    Task<AuthResult> signInWithGoogle(String idToken);
    void logout();
    User getCurrentUser();
    boolean isLoggedIn();
    Task<Boolean> isAdmin(String uid);
    Task<Void> createUserProfile(User user);
    Task<User> getUserProfile(String uid);
    Task<Void> deleteUserProfile(String uid);
}
