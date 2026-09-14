package com.cynorra.kpssarena.data.repository;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import android.os.Looper;

import com.cynorra.kpssarena.data.remote.FirebaseAuthSource;
import com.cynorra.kpssarena.data.remote.FirebaseFirestoreSource;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Probe: does a repository's internal Task#continueWith(...) (no explicit
 * Executor, as production code writes it) actually resolve under plain
 * JUnit (no Robolectric)?
 *
 * Findings from this probe:
 * 1. Tasks.await(...) unconditionally refuses to run when it thinks it's on
 *    the main thread. With testOptions.unitTests.returnDefaultValues=true,
 *    the stubbed Looper.myLooper() and Looper.getMainLooper() both return
 *    null, so Play Services sees null == null and wrongly concludes the
 *    JUnit test thread is the main thread -> IllegalStateException, before
 *    it even looks at whether the task is complete. Fixed here by statically
 *    mocking Looper.getMainLooper() to a distinct non-null value.
 * 2. Separately, continueWith(...) with no explicit Executor defaults to
 *    TaskExecutors.MAIN_THREAD, which posts through a real android.os.Handler
 *    that is a no-op stub outside Robolectric/a device, so the continuation
 *    would never actually run. Fixed at the source (AuthRepositoryImpl) by
 *    passing a direct Executor for continuations that don't need to run on
 *    the main thread.
 *
 * Every repository test that calls Tasks.await(...) needs both the direct
 * Executor at the production call site and this Looper static mock.
 */
public class ContinueWithProbeTest {

    @Test
    public void isAdmin_continueWith_resolvesUnderPlainJUnit() throws ExecutionException, InterruptedException, TimeoutException {
        try (MockedStatic<Looper> looperMock = mockStatic(Looper.class)) {
            looperMock.when(Looper::getMainLooper).thenReturn(mock(Looper.class));

            FirebaseAuthSource authSource = mock(FirebaseAuthSource.class);
            FirebaseFirestoreSource firestoreSource = mock(FirebaseFirestoreSource.class);
            FirebaseAnalytics analytics = mock(FirebaseAnalytics.class);

            DocumentSnapshot snapshot = mock(DocumentSnapshot.class);
            when(snapshot.exists()).thenReturn(true);
            when(snapshot.getBoolean("isAdmin")).thenReturn(true);
            when(firestoreSource.getUserClaims("uid1")).thenReturn(Tasks.forResult(snapshot));

            AuthRepositoryImpl repository = new AuthRepositoryImpl(authSource, firestoreSource, analytics);

            Task<Boolean> result = repository.isAdmin("uid1");
            Boolean isAdmin = Tasks.await(result, 5, TimeUnit.SECONDS);

            assertTrue(isAdmin);
        }
    }
}
