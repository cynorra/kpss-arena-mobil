package com.cynorra.kpssarena.domain.repository;
import com.google.android.gms.tasks.Task;
public interface PremiumRepository {
    Task<Void> purchaseMonthly(String uid);
    Task<Void> purchaseYearly(String uid);
    Task<Boolean> restorePurchases(String uid);
}
