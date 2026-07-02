package com.cynorra.kpssarena.data.remote;
import com.cynorra.kpssarena.domain.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
public class FirebaseAuthSource {
    private final FirebaseAuth auth;
    @Inject
    public FirebaseAuthSource(FirebaseAuth auth) {
        this.auth = auth;
    }
    public Task<AuthResult> signInWithEmail(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }
    public Task<AuthResult> createUserWithEmail(String email, String password) {
        return auth.createUserWithEmailAndPassword(email, password);
    }
    public Task<AuthResult> signInWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        return auth.signInWithCredential(credential);
    }
    public Task<Void> sendPasswordResetEmail(String email) {

        return auth.sendPasswordResetEmail(email);
    }
    public void signOut() {
        auth.signOut();
    }
    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }
    public User getCurrentUser() {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) return null;
        User user = new User();
        user.setUid(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());
        user.setPhotoUrl(firebaseUser.getPhotoUrl() != null ? 
                firebaseUser.getPhotoUrl().toString() : null);
        user.setUsername(firebaseUser.getDisplayName());
        return user;
    }
    public String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }
}