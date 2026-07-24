package com.cynorra.kpssarena.presentation.splash;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
@HiltViewModel
public class SplashViewModel extends BaseViewModel {
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_ONBOARDING_COMPLETED = "onboarding_completed";
    private final MutableLiveData<NavigationDestination> navigationDestination = new MutableLiveData<>();
    private final SharedPreferences prefs;
    private final FirebaseAuth firebaseAuth;
    public enum NavigationDestination {
        ONBOARDING,
        LOGIN,
        HOME
    }
    @Inject
    public SplashViewModel(@ApplicationContext Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.firebaseAuth = FirebaseAuth.getInstance();

        determineNavigation();
    }
    private void determineNavigation() {
        boolean onboardingCompleted = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false);

        if (!onboardingCompleted) {
            navigationDestination.setValue(NavigationDestination.ONBOARDING);
            return;
        }
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            navigationDestination.setValue(NavigationDestination.HOME);
        } else {
            navigationDestination.setValue(NavigationDestination.LOGIN);
        }
    }
    public void setOnboardingCompleted() {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply();
    }
    public LiveData<NavigationDestination> getNavigationDestination() {
        return navigationDestination;
    }
}
