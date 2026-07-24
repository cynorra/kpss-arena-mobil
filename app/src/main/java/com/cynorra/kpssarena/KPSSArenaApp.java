package com.cynorra.kpssarena;
import android.app.Application;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.work.Configuration;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.revenuecat.purchases.Purchases;
import com.revenuecat.purchases.PurchasesConfiguration;
import dagger.hilt.android.HiltAndroidApp;
import timber.log.Timber;
@HiltAndroidApp
public class KPSSArenaApp extends Application implements Configuration.Provider {
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
        // RevenueCat
        Purchases.configure(new PurchasesConfiguration.Builder(this, "appl_placeholder_key").build());
    }
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setWorkerFactory(new HiltWorkerFactory(this))
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
