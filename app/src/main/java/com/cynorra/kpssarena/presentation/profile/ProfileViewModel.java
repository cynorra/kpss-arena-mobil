package com.cynorra.kpssarena.presentation.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.domain.model.User;
import com.cynorra.kpssarena.domain.repository.AuthRepository;
import com.cynorra.kpssarena.domain.repository.ProfileRepository;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import com.google.firebase.auth.FirebaseAuth;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
@HiltViewModel
public class ProfileViewModel extends BaseViewModel {
    private final ProfileRepository profileRepository;
    private final AuthRepository authRepository;
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navigateToAdmin = new MutableLiveData<>(
    private final MutableLiveData<Boolean> logoutEvent = new MutableLiveData<>();
    @Inject
    public ProfileViewModel(ProfileRepository profileRepository, AuthRepository authRep
        this.profileRepository = profileRepository;
        this.authRepository = authRepository;
        loadUserProfile();
    }
    private void loadUserProfile() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        profileRepository.getProfile(uid)
                .addOnSuccessListener(userProfile -> user.postValue(userProfile))
                .addOnFailureListener(e -> setError("Profil yüklenemedi."));
    }
    public void checkAdminAccess() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        authRepository.isAdmin(uid)

                .addOnSuccessListener(isAdmin -> {
                    if (isAdmin) {
                        navigateToAdmin.postValue(true);
                    } else {
                        setError("Admin yetkisi bulunamadı.");
                    }
                });
    }
    public void logout() {
        authRepository.logout();
        logoutEvent.postValue(true);
    }
    public void onAdminNavigationDone() {
        navigateToAdmin.postValue(false);
    }
    public LiveData<User> getUser() { return user; }
    public LiveData<Boolean> getNavigateToAdmin() { return navigateToAdmin; }
    public LiveData<Boolean> getLogoutEvent() { return logoutEvent; }
}
