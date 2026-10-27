package com.cynorra.kpssarena.notification;

import android.Manifest;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

/**
 * Asks for the Android 13+ notification permission from a fragment and reports the outcome.
 * Must be created as a field of the fragment (registration has to happen before the fragment is STARTED).
 */
public class ReminderPermissionRequester {
    public interface Callback {
        void onResult(boolean notificationsAllowed);
    }

    private final Fragment fragment;
    private final Callback callback;
    private final ActivityResultLauncher<String> launcher;

    public ReminderPermissionRequester(Fragment fragment, Callback callback) {
        this.fragment = fragment;
        this.callback = callback;
        this.launcher = fragment.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> callback.onResult(Boolean.TRUE.equals(granted) && NotificationHelper.canNotify(fragment.requireContext())));
    }

    public void request() {
        if (Build.VERSION.SDK_INT >= 33 && !NotificationHelper.canNotify(fragment.requireContext())) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS);
        } else {
            callback.onResult(NotificationHelper.canNotify(fragment.requireContext()));
        }
    }
}
