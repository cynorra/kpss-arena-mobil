package com.cynorra.kpssarena.presentation.premium;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.domain.repository.PremiumRepository;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import com.google.firebase.auth.FirebaseAuth;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
@HiltViewModel
public class PremiumViewModel extends BaseViewModel {
    private final PremiumRepository premiumRepository;
    private final MutableLiveData<Boolean> isPremium = new MutableLiveData<>();
    @Inject
    public PremiumViewModel(PremiumRepository premiumRepository) {
        this.premiumRepository = premiumRepository;
    }
    public void purchaseMonthly() {
        setLoading(true);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        premiumRepository.purchaseMonthly(uid)
                .addOnSuccessListener(result -> {
                    setLoading(false);
                    isPremium.setValue(true);
                    setSuccess("Aylık premium üyelik başlatıldı!");
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError("Satın alma başarısız oldu.");
                });
    }
    public void purchaseYearly() {
        setLoading(true);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        premiumRepository.purchaseYearly(uid)
                .addOnSuccessListener(result -> {
                    setLoading(false);
                    isPremium.setValue(true);
                    setSuccess("Yıllık premium üyelik başlatıldı!");
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError("Satın alma başarısız oldu.");
                });
    }
    public void restorePurchases() {
        setLoading(true);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        premiumRepository.restorePurchases(uid)
                .addOnSuccessListener(isPremiumUser -> {
                    setLoading(false);
                    isPremium.setValue(isPremiumUser);
                    if (isPremiumUser) {
                        setSuccess("Premium üyeliğiniz geri yüklendi!");
                    } else {
                        setError("Aktif premium üyeliğiniz bulunamadı.");
                    }
                })

                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError("Geri yükleme başarısız.");
                });
    }
    public LiveData<Boolean> getIsPremium() { return isPremium; }
}
