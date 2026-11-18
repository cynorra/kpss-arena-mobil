package com.cynorra.kpssarena;
import android.app.Application;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.work.Configuration;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import dagger.hilt.android.HiltAndroidApp;
import javax.inject.Inject;
import timber.log.Timber;
@HiltAndroidApp
public class KPSSArenaApp extends Application implements Configuration.Provider {
    @Inject
    HiltWorkerFactory workerFactory;
    @Override
    public void onCreate() {
        super.onCreate();

        // Timber initialization
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
        // Firebase Crashlytics
        try {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG);
        } catch (Exception e) {
            Timber.w(e, "FirebaseCrashlytics initialization skipped: %s", e.getMessage());
        }
        // Google Mobile Ads SDK
        MobileAds.initialize(this, status -> {
            com.google.android.gms.ads.RequestConfiguration requestConfiguration =
                    new com.google.android.gms.ads.RequestConfiguration.Builder()
                            .setTestDeviceIds(java.util.Collections.singletonList(com.google.android.gms.ads.AdRequest.DEVICE_ID_EMULATOR))
                            .build();
            MobileAds.setRequestConfiguration(requestConfiguration);
        });
        // Daily reminder: re-assert the saved opt-in schedule (e.g. after an app update)
        com.cynorra.kpssarena.notification.ReminderScheduler.sync(this);
        // The channel must exist before a background push arrives (FCM uses it by id)
        com.cynorra.kpssarena.notification.NotificationHelper.ensureChannel(this);
        // Which friend's invite link brought this install (read once per install)
        com.cynorra.kpssarena.engagement.InviteHelper.captureInstallReferrer(this);
        // Server-sent announcements (new test, events) reach everyone subscribed to this topic
        try {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("all");
        } catch (Exception e) {
            Timber.w(e, "FirebaseMessaging subscription failed: %s", e.getMessage());
        }
    }
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build();
    }
    private static class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority >= android.util.Log.ERROR && t != null) {
                FirebaseCrashlytics.getInstance().recordException(t);
            }
        }
    }
}
