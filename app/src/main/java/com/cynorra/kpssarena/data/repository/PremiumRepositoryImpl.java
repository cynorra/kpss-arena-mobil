package com.cynorra.kpssarena.data.repository;
import com.cynorra.kpssarena.data.remote.FirebaseFirestoreSource;
import com.cynorra.kpssarena.domain.repository.PremiumRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
public class PremiumRepositoryImpl implements PremiumRepository {
    private final FirebaseFirestoreSource firestoreSource;
    private final FirebaseAnalytics analytics;
    @Inject
    public PremiumRepositoryImpl(FirebaseFirestoreSource firestoreSource, FirebaseAnalytics analytics) {
        this.firestoreSource = firestoreSource;
        this.analytics = analytics;
    }
    // NOTE: The actual RevenueCat purchase() call requires an Activity + a Package resolved
    // from Purchases.getSharedInstance().getOfferings(...), neither of which are available at
    // the repository layer (this method only receives a uid). That in-app-purchase trigger
    // belongs in the Fragment/Activity layer (out of scope here). This method represents the
    // post-purchase bookkeeping step: marking the user's Firestore profile as premium once a
    // purchase has completed.
    @Override
    public Task<Void> purchaseMonthly(String uid) {
        analytics.logEvent("purchase_monthly", null);
        return markPremium(uid);
    }
    @Override
    public Task<Void> purchaseYearly(String uid) {
        analytics.logEvent("purchase_yearly", null);
        return markPremium(uid);
    }
    @Override
    public Task<Boolean> restorePurchases(String uid) {
        analytics.logEvent("restore_purchases", null);
        return firestoreSource.getUserProfile(uid)
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        Boolean premium = task.getResult().getBoolean("premium");
                        return Boolean.TRUE.equals(premium);
                    }
                    return false;
                });
    }
    private Task<Void> markPremium(String uid) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("premium", true);
        return firestoreSource.updateUserProfile(uid, updates);
    }
}
