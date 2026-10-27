package com.cynorra.kpssarena.di;
import android.content.Context;
import androidx.room.Room;
import com.cynorra.kpssarena.data.local.AppDatabase;
import com.cynorra.kpssarena.data.local.dao.MistakeDao;
import com.cynorra.kpssarena.data.local.dao.UserCacheDao;
import com.cynorra.kpssarena.presentation.common.ConsentManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.database.FirebaseDatabase;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import timber.log.Timber;
@Module
@InstallIn(SingletonComponent.class)
public class AppModule {
    @Provides
    @Singleton
    public static FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }
    @Provides

    @Singleton
    public static FirebaseFirestore provideFirebaseFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        try {
            firestore.setFirestoreSettings(settings);
        } catch (IllegalStateException e) {
            // Firestore was already used elsewhere; keep its current settings instead of crashing at startup.
        }
        return firestore;
    }
    @Provides
    @Singleton
    public static FirebaseDatabase provideFirebaseDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        try {
            database.setPersistenceEnabled(true);
        } catch (com.google.firebase.database.DatabaseException e) {
            // Must be set before first use; ignore if the database was already initialised.
        }
        return database;
    }
    @Provides
    @Singleton
    public static FirebaseAnalytics provideFirebaseAnalytics(@ApplicationContext Context context) {
        return FirebaseAnalytics.getInstance(context);
    }
    @Provides
    @Singleton
    public static AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                "kpss_arena_db"
        )
                .fallbackToDestructiveMigration()
                .build();
    }
    @Provides
    @Singleton
    public static MistakeDao provideMistakeDao(AppDatabase database) {

        return database.mistakeDao();
    }
    @Provides
    @Singleton
    public static UserCacheDao provideUserCacheDao(AppDatabase database) {
        return database.userCacheDao();
    }
    @Provides
    @Singleton
    public static ConsentManager provideConsentManager(@ApplicationContext Context context) {
        return new ConsentManager(context);
    }
}
