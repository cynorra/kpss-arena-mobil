package com.cynorra.kpssarena.presentation.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.cynorra.kpssarena.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

/**
 * Play-Policy-Compliant Smart In-App Review helper:
 * - Never gates or asks for "5 stars"
 * - Triggers on 2nd+ launch after 6s delay, 21-day cooldown, max 2 prompts
 * - Permanently suppressed once rated or "Don't ask again" is tapped.
 */
public final class ReviewPromptHelper {

    private static final String PREFS = "kpss_review_prompt";
    private static final String K_LAUNCHES = "launches";
    private static final String K_SHOWN_COUNT = "shown_count";
    private static final String K_LAST_SHOWN_MS = "last_shown_ms";
    private static final String K_DISMISSED_FOREVER = "dismissed_forever";
    private static final long COOLDOWN_MS = 21L * 24 * 60 * 60 * 1000L;
    private static final int MAX_PROMPTS = 2;

    private ReviewPromptHelper() {}

    public static void trackLaunchAndMaybePrompt(Activity activity) {
        if (activity == null || activity.isFinishing()) return;
        SharedPreferences sp = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (sp.getBoolean(K_DISMISSED_FOREVER, false)) return;

        int launches = sp.getInt(K_LAUNCHES, 0) + 1;
        sp.edit().putInt(K_LAUNCHES, launches).apply();

        int shown = sp.getInt(K_SHOWN_COUNT, 0);
        long lastShown = sp.getLong(K_LAST_SHOWN_MS, 0L);
        long now = System.currentTimeMillis();

        if (launches < 2 || shown >= MAX_PROMPTS) return;
        if (lastShown > 0 && (now - lastShown) < COOLDOWN_MS) return;

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (activity.isFinishing() || activity.isDestroyed()) return;
            sp.edit().putInt(K_SHOWN_COUNT, shown + 1).putLong(K_LAST_SHOWN_MS, System.currentTimeMillis()).apply();
            new MaterialAlertDialogBuilder(activity)
                    .setTitle(R.string.review_prompt_title)
                    .setMessage(R.string.review_prompt_message)
                    .setPositiveButton(R.string.review_prompt_rate, (d, w) -> {
                        sp.edit().putBoolean(K_DISMISSED_FOREVER, true).apply();
                        launchPlayReview(activity);
                    })
                    .setNeutralButton(R.string.review_prompt_later, null)
                    .setNegativeButton(R.string.review_prompt_never, (d, w) ->
                            sp.edit().putBoolean(K_DISMISSED_FOREVER, true).apply())
                    .show();
        }, 6000L);
    }

    private static void launchPlayReview(Activity activity) {
        ReviewManager manager = ReviewManagerFactory.create(activity);
        manager.requestReviewFlow().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !activity.isFinishing()) {
                manager.launchReviewFlow(activity, task.getResult());
            }
        });
    }
}
