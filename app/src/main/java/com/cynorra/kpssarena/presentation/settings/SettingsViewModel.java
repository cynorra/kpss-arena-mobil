package com.cynorra.kpssarena.presentation.settings;
import android.content.Context;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.domain.repository.AuthRepository;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import com.google.firebase.auth.FirebaseAuth;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
@HiltViewModel
public class SettingsViewModel extends BaseViewModel {
    private final AuthRepository authRepository;
    private final Context context;
    @Inject
    public SettingsViewModel(AuthRepository authRepository, @ApplicationContext Context context) {
        this.authRepository = authRepository;
        this.context = context;
    }
    public void deleteAccount() {
        setLoading(true);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        
        // Delete Firestore user data
        // Delete Auth account
        FirebaseAuth.getInstance().getCurrentUser().delete()
                .addOnSuccessListener(v -> {
                    setLoading(false);
                    authRepository.logout();
                    setSuccess(context.getString(R.string.success_account_deleted));
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError(context.getString(R.string.error_account_delete_failed, e.getMessage()));
                });
    }
}
