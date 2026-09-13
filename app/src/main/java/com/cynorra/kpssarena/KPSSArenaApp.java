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
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG);
        // Google Mobile Ads SDK
        MobileAds.initialize(this);
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
