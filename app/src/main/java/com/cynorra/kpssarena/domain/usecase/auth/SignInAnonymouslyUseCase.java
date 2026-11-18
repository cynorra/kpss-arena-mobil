package com.cynorra.kpssarena.domain.usecase.auth;

import com.cynorra.kpssarena.domain.model.User;
import com.cynorra.kpssarena.domain.repository.AuthRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import javax.inject.Inject;
import timber.log.Timber;

public class SignInAnonymouslyUseCase {

    private final AuthRepository authRepository;

    @Inject
    public SignInAnonymouslyUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public Task<AuthResult> execute() {
        return authRepository.signInAnonymously()
                .continueWithTask(task -> {
                    if (!task.isSuccessful() || task.getResult() == null) {
                        return task;
                    }
                    AuthResult authResult = task.getResult();
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        return Tasks.forResult(authResult);
                    }
                    String uid = firebaseUser.getUid();
                    String shortId = uid.length() > 4 ? uid.substring(0, 4).toUpperCase() : uid;
                    String displayName = "Misafir Aday #" + shortId;

                    return authRepository.getUserProfile(uid)
                            .continueWithTask(profileTask -> {
                                User existing = profileTask.isSuccessful() ? profileTask.getResult() : null;
                                if (existing != null) {
                                    return Tasks.forResult(authResult);
                                }
                                User user = new User(
                                        uid,
                                        displayName,
                                        "",
                                        null,
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
                                                        "Failed to create anonymous profile for uid=%s", uid);
                                            }
                                            return authResult;
                                        });
                            });
                });
    }
}
