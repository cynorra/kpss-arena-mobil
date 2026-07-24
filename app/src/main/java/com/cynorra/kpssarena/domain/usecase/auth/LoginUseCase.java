package com.cynorra.kpssarena.domain.usecase.auth;
import com.cynorra.kpssarena.domain.repository.AuthRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import javax.inject.Inject;
public class LoginUseCase {
    private final AuthRepository authRepository;
    @Inject
    public LoginUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }
    public Task<AuthResult> execute(String email, String password) {
        return authRepository.loginWithEmail(email, password);
    }
}
