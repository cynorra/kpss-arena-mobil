package com.cynorra.kpssarena.domain.repository;
import com.cynorra.kpssarena.domain.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
public interface AuthRepository {
    Task<AuthResult> loginWithEmail(String email, String password);
    Task<AuthResult> registerWithEmail(String email, String password);
    Task<AuthResult> signInWithGoogle(String idToken);
    Task<Void> sendPasswordResetEmail(String email);
    void logout();
    User getCurrentUser();
    boolean isLoggedIn();
    Task<Boolean> isAdmin(String uid);
    Task<Void> createUserProfile(User user);
    Task<User> getUserProfile(String uid);
}
