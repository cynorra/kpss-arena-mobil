package com.cynorra.kpssarena.presentation.auth;
import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.domain.model.User;
import com.cynorra.kpssarena.domain.usecase.auth.GoogleSignInUseCase;
import com.cynorra.kpssarena.domain.usecase.auth.SignInAnonymouslyUseCase;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
@HiltViewModel
public class AuthViewModel extends BaseViewModel {
    private final GoogleSignInUseCase googleSignInUseCase;
    private final SignInAnonymouslyUseCase signInAnonymouslyUseCase;
    private final Context context;
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> navigateToHome = new MutableLiveData<>(false);
    @Inject
    public AuthViewModel(GoogleSignInUseCase googleSignInUseCase,
                         SignInAnonymouslyUseCase signInAnonymouslyUseCase,
                         @ApplicationContext Context context) {
        this.googleSignInUseCase = googleSignInUseCase;
        this.signInAnonymouslyUseCase = signInAnonymouslyUseCase;
        this.context = context;
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
                    setError(context.getString(R.string.error_google_sign_in_generic));
                });
    }
    public void signInAnonymously() {
        setLoading(true);
        signInAnonymouslyUseCase.execute()
                .addOnSuccessListener(authResult -> {
                    setLoading(false);
                    isLoggedIn.setValue(true);
                    navigateToHome.setValue(true);
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError(context.getString(R.string.error_anonymous_sign_in_failed));
                });
    }
    public void onNavigationDone() {
        navigateToHome.setValue(false);
    }
}
