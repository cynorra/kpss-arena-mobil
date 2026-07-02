package com.cynorra.kpssarena.presentation.auth;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cynorra.kpssarena.domain.model.User;
import com.cynorra.kpssarena.domain.usecase.auth.GoogleSignInUseCase;
import com.cynorra.kpssarena.domain.usecase.auth.LoginUseCase;
import com.cynorra.kpssarena.domain.usecase.auth.RegisterUseCase;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import com.google.firebase.auth.AuthResult;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
@HiltViewModel
public class AuthViewModel extends BaseViewModel {
    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final GoogleSignInUseCase googleSignInUseCase;
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> navigateToHome = new MutableLiveData<>(f
    @Inject
    public AuthViewModel(LoginUseCase loginUseCase,
                         RegisterUseCase registerUseCase,
                         GoogleSignInUseCase googleSignInUseCase) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
        this.googleSignInUseCase = googleSignInUseCase;
    }
    public LiveData<User> getUser() {
        return userLiveData;
    }
    public LiveData<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }

    public LiveData<Boolean> getNavigateToHome() {
        return navigateToHome;
    }
    public void login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            setError("E-posta ve şifre alanları boş bırakılamaz.");
            return;
        }
        if (password.length() < 6) {
            setError("Şifre en az 6 karakter olmalıdır.");
            return;
        }
        setLoading(true);
        loginUseCase.execute(email, password)
                .addOnSuccessListener(authResult -> {
                    setLoading(false);
                    isLoggedIn.setValue(true);
                    navigateToHome.setValue(true);
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError(getAuthErrorMessage(e.getMessage()));
                });
    }
    public void register(String email, String password, String username) {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            setError("Tüm alanlar zorunludur.");
            return;
        }
        if (password.length() < 6) {
            setError("Şifre en az 6 karakter olmalıdır.");
            return;
        }
        if (username.length() < 3) {

            setError("Kullanıcı adı en az 3 karakter olmalıdır.");
            return;
        }
        setLoading(true);
        registerUseCase.execute(email, password, username)
                .addOnSuccessListener(authResult -> {
                    setLoading(false);
                    isLoggedIn.setValue(true);
                    navigateToHome.setValue(true);
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError(getAuthErrorMessage(e.getMessage()));
                });
    }
    public void signInWithGoogle(String idToken) {
        setLoading(true);
        googleSignInUseCase.execute(idToken)
                .addOnSuccessListener(authResult -> {
                    setLoading(false);
                    isLoggedIn.setValue(true);
                    navigateToHome.setValue(true);
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError("Google ile giriş yapılamadı.");
                });
    }
    public void onNavigationDone() {
        navigateToHome.setValue(false);
    }
    private String getAuthErrorMessage(String message) {
        if (message == null) return "Bilinmeyen bir hata oluştu.";
        if (message.contains("email address is badly formatted")) {
            return "Geçersiz e-posta formatı.";

        } else if (message.contains("There is no user record")) {
            return "Bu e-posta ile kayıtlı kullanıcı bulunamadı.";
        } else if (message.contains("password is invalid")) {
            return "Hatalı şifre girdiniz.";
        } else if (message.contains("email address is already in use")) {
            return "Bu e-posta adresi zaten kullanımda.";
        } else if (message.contains("network error")) {
            return "İnternet bağlantınızı kontrol edin.";
        } else {
            return "Giriş yapılırken bir hata oluştu. Lütfen tekrar deneyin.";
        }
    }
}
