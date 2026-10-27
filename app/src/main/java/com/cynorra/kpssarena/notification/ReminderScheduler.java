package com.cynorra.kpssarena.notification;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/** Opt-in daily reminder: a WorkManager job that fires shortly after the 09:00 daily test is published. */
public final class ReminderScheduler {
    private static final String PREFS = "notification_prefs";
    private static final String KEY_ENABLED = "daily_reminder_enabled";
    private static final String KEY_PROMPTED = "daily_reminder_prompted";
    private static final String WORK_NAME = "daily_test_reminder";
    private static final int REMINDER_HOUR = 9;
    private static final int REMINDER_MINUTE = 10;

    private ReminderScheduler() {}

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static boolean isEnabled(Context context) {
        return prefs(context).getBoolean(KEY_ENABLED, false);
    }

    public static boolean wasPrompted(Context context) {
        return prefs(context).getBoolean(KEY_PROMPTED, false);
    }

    public static void markPrompted(Context context) {
        prefs(context).edit().putBoolean(KEY_PROMPTED, true).apply();
    }

    public static void setEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply();
        if (enabled) {
            NotificationHelper.ensureChannel(context);
            schedule(context);
        } else {
            WorkManager.getInstance(context.getApplicationContext()).cancelUniqueWork(WORK_NAME);
        }
    }

    /** Keeps the schedule consistent with the saved choice (e.g. after an app update). */
    public static void sync(Context context) {
        if (isEnabled(context)) {
            schedule(context);
        }
    }

    private static void schedule(Context context) {
        Calendar next = Calendar.getInstance();
        next.set(Calendar.HOUR_OF_DAY, REMINDER_HOUR);
        next.set(Calendar.MINUTE, REMINDER_MINUTE);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        if (next.getTimeInMillis() <= System.currentTimeMillis()) {
            next.add(Calendar.DAY_OF_YEAR, 1);
        }
        long delay = next.getTimeInMillis() - System.currentTimeMillis();
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(DailyReminderWorker.class, 24, TimeUnit.HOURS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build();
        WorkManager.getInstance(context.getApplicationContext())
                .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.UPDATE, request);
    }
}
