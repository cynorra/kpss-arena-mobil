package com.cynorra.kpssarena.di;
import com.cynorra.kpssarena.data.local.dao.MistakeDao;
import com.cynorra.kpssarena.data.local.dao.UserCacheDao;
import com.cynorra.kpssarena.data.remote.FirebaseAuthSource;
import com.cynorra.kpssarena.data.remote.FirebaseFirestoreSource;
import com.cynorra.kpssarena.data.remote.FirebaseRealtimeSource;
import com.cynorra.kpssarena.data.repository.AuthRepositoryImpl;
import com.cynorra.kpssarena.data.repository.LeaderboardRepositoryImpl;
import com.cynorra.kpssarena.data.repository.MatchRepositoryImpl;
import com.cynorra.kpssarena.data.repository.MistakeRepositoryImpl;
import com.cynorra.kpssarena.data.repository.PremiumRepositoryImpl;
import com.cynorra.kpssarena.data.repository.ProfileRepositoryImpl;
import com.cynorra.kpssarena.data.repository.StatsRepositoryImpl;
import com.cynorra.kpssarena.data.repository.TestRepositoryImpl;
import com.cynorra.kpssarena.domain.repository.AuthRepository;
import com.cynorra.kpssarena.domain.repository.LeaderboardRepository;
import com.cynorra.kpssarena.domain.repository.MatchRepository;
import com.cynorra.kpssarena.domain.repository.MistakeRepository;

import com.cynorra.kpssarena.domain.repository.PremiumRepository;
import com.cynorra.kpssarena.domain.repository.ProfileRepository;
import com.cynorra.kpssarena.domain.repository.StatsRepository;
import com.cynorra.kpssarena.domain.repository.TestRepository;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
@Module
@InstallIn(SingletonComponent.class)
public class RepositoryModule {
    @Provides
    @Singleton
    public static FirebaseAuthSource provideFirebaseAuthSource(FirebaseAuth auth) {
        return new FirebaseAuthSource(auth);
    }
    @Provides
    @Singleton
    public static FirebaseFirestoreSource provideFirebaseFirestoreSource(FirebaseFirestofi
        return new FirebaseFirestoreSource(firestore);
    }
    @Provides
    @Singleton
    public static FirebaseRealtimeSource provideFirebaseRealtimeSource(FirebaseDataba
        return new FirebaseRealtimeSource(database);
    }
    @Provides
    @Singleton

    public static AuthRepository provideAuthRepository(
            FirebaseAuthSource authSource,
            FirebaseFirestoreSource firestoreSource,
            FirebaseAnalytics analytics) {
        return new AuthRepositoryImpl(authSource, firestoreSource, analytics);
    }
    @Provides
    @Singleton
    public static TestRepository provideTestRepository(
            FirebaseFirestoreSource firestoreSource,
            UserCacheDao userCacheDao,
            FirebaseAnalytics analytics) {
        return new TestRepositoryImpl(firestoreSource, userCacheDao, analytics);
    }
    @Provides
    @Singleton
    public static LeaderboardRepository provideLeaderboardRepository(
            FirebaseFirestoreSource firestoreSource,
            FirebaseAnalytics analytics) {
        return new LeaderboardRepositoryImpl(firestoreSource, analytics);
    }
    @Provides
    @Singleton
    public static MatchRepository provideMatchRepository(
            FirebaseFirestoreSource firestoreSource,
            FirebaseRealtimeSource realtimeSource,
            FirebaseAnalytics analytics) {
        return new MatchRepositoryImpl(firestoreSource, realtimeSource, analytics);
    }
    @Provides
    @Singleton
    public static MistakeRepository provideMistakeRepository(
            MistakeDao mistakeDao,
            FirebaseFirestoreSource firestoreSource) {
        return new MistakeRepositoryImpl(mistakeDao, firestoreSource);
    }

    @Provides
    @Singleton
    public static StatsRepository provideStatsRepository(
            FirebaseFirestoreSource firestoreSource,
            UserCacheDao userCacheDao) {
        return new StatsRepositoryImpl(firestoreSource, userCacheDao);
    }
    @Provides
    @Singleton
    public static ProfileRepository provideProfileRepository(
            FirebaseFirestoreSource firestoreSource,
            UserCacheDao userCacheDao) {
        return new ProfileRepositoryImpl(firestoreSource, userCacheDao);
    }
    @Provides
    @Singleton
    public static PremiumRepository providePremiumRepository(
            FirebaseFirestoreSource firestoreSource,
            FirebaseAnalytics analytics) {
        return new PremiumRepositoryImpl(firestoreSource, analytics);
    }
}
