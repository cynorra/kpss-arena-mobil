package com.cynorra.kpssarena.presentation.settings;
import com.cynorra.kpssarena.domain.repository.AuthRepository;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import com.google.firebase.auth.FirebaseAuth;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
@HiltViewModel
public class SettingsViewModel extends BaseViewModel {
    private final AuthRepository authRepository;
    @Inject
    public SettingsViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
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
                    setSuccess("Hesabınız silindi.");
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError("Hesap silinirken hata oluştu: " + e.getMessage());
                });
    }
}
