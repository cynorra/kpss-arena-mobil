package com.cynorra.kpssarena.presentation.profile;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.domain.model.User;
import com.cynorra.kpssarena.domain.repository.AuthRepository;
import com.cynorra.kpssarena.domain.repository.ProfileRepository;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import com.google.firebase.auth.FirebaseAuth;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
@HiltViewModel
public class ProfileViewModel extends BaseViewModel {
    private final ProfileRepository profileRepository;
    private final AuthRepository authRepository;
    private final Context context;
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navigateToAdmin = new MutableLiveData<>();
    private final MutableLiveData<Boolean> logoutEvent = new MutableLiveData<>();
    @Inject
    public ProfileViewModel(ProfileRepository profileRepository, AuthRepository authRepository,
                             @ApplicationContext Context context) {
        this.profileRepository = profileRepository;
        this.authRepository = authRepository;
        this.context = context;
        loadUserProfile();
    }
    private void loadUserProfile() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            setError(context.getString(R.string.error_no_session));
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        profileRepository.getProfile(uid)
                .addOnSuccessListener(userProfile -> user.postValue(userProfile))
                .addOnFailureListener(e -> setError(context.getString(R.string.error_profile_load_failed_generic)));
    }
    public void checkAdminAccess() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            setError(context.getString(R.string.error_no_session));
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        authRepository.isAdmin(uid)
                .addOnSuccessListener(isAdmin -> {
                    if (isAdmin) {
                        navigateToAdmin.postValue(true);
                    } else {
                        setError(context.getString(R.string.error_admin_access_denied));
                    }
                })
                .addOnFailureListener(e -> setError(context.getString(R.string.error_permission_check_failed)));
    }
    public void logout() {
        authRepository.logout();
        logoutEvent.postValue(true);
    }
    public void onAdminNavigationDone() {
        navigateToAdmin.postValue(false);
    }
    public void loadOtherUserProfile(String userId) {
        profileRepository.getProfile(userId)
                .addOnSuccessListener(userProfile -> user.postValue(userProfile))
                .addOnFailureListener(e -> setError(context.getString(R.string.error_user_profile_load_failed)));
    }
    public void toggleFollow(String targetUserId) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            setError(context.getString(R.string.error_no_session));
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        profileRepository.toggleFollow(uid, targetUserId)
                .addOnSuccessListener(aVoid -> loadOtherUserProfile(targetUserId))
                .addOnFailureListener(e -> setError(context.getString(R.string.error_action_failed)));
    }
    public LiveData<User> getUser() { return user; }
    public LiveData<Boolean> getNavigateToAdmin() { return navigateToAdmin; }
    public LiveData<Boolean> getLogoutEvent() { return logoutEvent; }
}
