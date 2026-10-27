package com.cynorra.kpssarena.presentation.profile;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.domain.model.User;
import com.cynorra.kpssarena.domain.repository.AuthRepository;
import com.cynorra.kpssarena.domain.repository.MatchRepository;
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
    private final MatchRepository matchRepository;
    private final Context context;
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navigateToAdmin = new MutableLiveData<>();
    private final MutableLiveData<Boolean> logoutEvent = new MutableLiveData<>();
    private final MutableLiveData<String> challengeMatchId = new MutableLiveData<>();
    @Inject
    public ProfileViewModel(ProfileRepository profileRepository, AuthRepository authRepository, MatchRepository matchRepository,
                             @ApplicationContext Context context) {
        this.profileRepository = profileRepository;
        this.authRepository = authRepository;
        this.matchRepository = matchRepository;
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
    public void updateUsername(String newUsername) {
        String name = newUsername == null ? "" : newUsername.trim();
        if (name.length() < 3 || name.length() > 20) {
            setError(context.getString(R.string.error_username_invalid));
            return;
        }
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            setError(context.getString(R.string.error_no_session));
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        profileRepository.updateUsername(uid, name)
                .addOnSuccessListener(aVoid -> {
                    setSuccess(context.getString(R.string.toast_profile_updated));
                    loadUserProfile();
                })
                .addOnFailureListener(e -> setError(e.getMessage() != null && e.getMessage().equals(context.getString(R.string.error_username_taken))
                        ? e.getMessage() : context.getString(R.string.error_action_failed)));
    }
    public void challengeUser(String username) {
        if (username == null || username.isEmpty()) return;
        matchRepository.challengeByUsername(username)
                .addOnSuccessListener(challengeMatchId::postValue)
                .addOnFailureListener(e -> setError(e.getMessage() != null && e.getMessage().equals(context.getString(R.string.error_cannot_challenge_self))
                        ? e.getMessage() : context.getString(R.string.error_user_not_found_or_unavailable)));
    }
    public void onChallengeHandled() { challengeMatchId.postValue(null); }
    public void consumeMessages() {
        clearSuccess();
        clearError();
    }
    public LiveData<String> getChallengeMatchId() { return challengeMatchId; }
    public LiveData<User> getUser() { return user; }
    public LiveData<Boolean> getNavigateToAdmin() { return navigateToAdmin; }
    public LiveData<Boolean> getLogoutEvent() { return logoutEvent; }
}
