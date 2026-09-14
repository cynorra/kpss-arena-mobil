package com.cynorra.kpssarena.data.repository;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.cynorra.kpssarena.data.remote.FirebaseAuthSource;
import com.cynorra.kpssarena.data.remote.FirebaseFirestoreSource;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Probe: does a repository's internal Task#continueWith(...) (no explicit
 * Executor, as production code writes it) actually resolve under plain
 * JUnit (no Robolectric)? If this hangs/times out, every repository test
 * relying on this pattern needs a different approach (explicit Executor
 * override in tests, or Robolectric). One cheap, isolated probe before
 * writing the full repository test suite.
 */
public class ContinueWithProbeTest {

    @Test
    public void isAdmin_continueWith_resolvesUnderPlainJUnit() throws ExecutionException, InterruptedException, TimeoutException {
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
