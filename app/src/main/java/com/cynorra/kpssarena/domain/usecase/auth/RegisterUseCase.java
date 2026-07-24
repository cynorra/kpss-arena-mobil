package com.cynorra.kpssarena.domain.usecase.auth;
import com.cynorra.kpssarena.domain.model.User;
import com.cynorra.kpssarena.domain.repository.AuthRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import java.util.Collections;
import javax.inject.Inject;
public class RegisterUseCase {
    private final AuthRepository authRepository;
    @Inject
    public RegisterUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }
    public Task<AuthResult> execute(String email, String password, String username) {
        return authRepository.registerWithEmail(email, password)
                .continueWithTask(task -> {
                    AuthResult authResult = task.getResult();
                    User user = new User(
                            authResult.getUser().getUid(),
                            username,
                            email,
                            null,
                            0, 0, 0, 0,
                            null,
                            Collections.emptyList(),
                            Collections.emptyList(),
                            Collections.emptyList(),
                            false,
                            System.currentTimeMillis());
                    return authRepository.createUserProfile(user)
                            .continueWith(profileTask -> authResult);
                });
    }
}
