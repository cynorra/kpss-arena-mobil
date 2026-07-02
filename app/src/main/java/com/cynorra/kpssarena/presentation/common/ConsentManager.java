package com.cynorra.kpssarena.presentation.common;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;
import javax.inject.Inject;

import javax.inject.Singleton;
import timber.log.Timber;
@Singleton
public class ConsentManager {
    private static final String PREFS_NAME = "consent_prefs";
    private static final String KEY_CONSENT_OBTAINED = "consent_obtained";
    private final Context context;
    private ConsentInformation consentInformation;
    @Inject
    public ConsentManager(Context context) {
        this.context = context.getApplicationContext();
    }
    public void initialize(Activity activity, ConsentCallback callback) {
        ConsentRequestParameters params;
        if (com.cynorra.kpssarena.BuildConfig.DEBUG) {
            ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(conte
                    .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEO
                    .addTestDeviceHashedId("TEST-DEVICE-HASHED-ID")
                    .build();
            params = new ConsentRequestParameters.Builder()
                    .setConsentDebugSettings(debugSettings)
                    .build();
        } else {
            params = new ConsentRequestParameters.Builder().build();
        }
        consentInformation = UserMessagingPlatform.getConsentInformation(context);
        consentInformation.requestConsentInfoUpdate(
                activity,
                params,
                () -> {
                    if (consentInformation.isConsentFormAvailable()) {
                        loadForm(activity, callback);

                    } else {
                        callback.onConsentNotRequired();
                    }
                },
                formError -> {
                    Timber.e("Consent info update failed: %s", formError.getMessage());
                    callback.onConsentNotRequired();
                }
        );
    }
    private void loadForm(Activity activity, ConsentCallback callback) {
        UserMessagingPlatform.loadConsentForm(
                context,
                consentForm -> {
                    consentForm.show(
                            activity,
                            formError -> {
                                if (formError == null) {
                                    saveConsentStatus();
                                    callback.onConsentObtained();
                                } else {
                                    callback.onConsentNotRequired();
                                }
                            }
                    );
                },
                formError -> {
                    Timber.e("Consent form load failed: %s", formError.getMessage());
                    callback.onConsentNotRequired();
                }
        );
    }
    private void saveConsentStatus() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.M
        prefs.edit().putBoolean(KEY_CONSENT_OBTAINED, true).apply();
    }
    public boolean isConsentObtained() {

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.M
        return prefs.getBoolean(KEY_CONSENT_OBTAINED, false);
    }
    public boolean canShowPersonalizedAds() {
        if (consentInformation == null) return false;
        return consentInformation.getConsentStatus() == ConsentInformation.ConsentStat
    }
    public interface ConsentCallback {
        void onConsentObtained();
        void onConsentNotRequired();
    }
}
