package com.cynorra.kpssarena.domain.usecase.auth;
import com.cynorra.kpssarena.domain.model.User;
import com.cynorra.kpssarena.domain.repository.AuthRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import java.util.Collections;
import javax.inject.Inject;
import timber.log.Timber;
public class GoogleSignInUseCase {
    private final AuthRepository authRepository;
    @Inject
    public GoogleSignInUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }
    public Task<AuthResult> execute(String idToken) {
        return authRepository.signInWithGoogle(idToken)
                .continueWithTask(task -> {
                    AuthResult authResult = task.getResult();
                    FirebaseUser firebaseUser = authResult.getUser();
                    return authRepository.getUserProfile(firebaseUser.getUid())
                            .continueWithTask(profileTask -> {
                                User existing = profileTask.isSuccessful() ? profileTask.getResult() : null;
                                if (existing != null) {
                                    return com.google.android.gms.tasks.Tasks.forResult(authResult);
                                }
                                User user = new User(
                                        firebaseUser.getUid(),
                                        firebaseUser.getDisplayName(),
                                        firebaseUser.getEmail(),
                                        firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null,
                                        0, 0, 0, 0,
                                        null,
                                        Collections.emptyList(),
                                        Collections.emptyList(),
                                        Collections.emptyList(),
                                        false,
                                        System.currentTimeMillis());
                                return authRepository.createUserProfile(user)
                                        .continueWith(createTask -> {
                                            if (!createTask.isSuccessful()) {
                                                Timber.e(createTask.getException(),
                                                        "Failed to create user profile after Google sign-in for uid=%s",
                                                        firebaseUser.getUid());
                                            }
                                            return authResult;
                                        });
                            });
                });
    }
}
