package com.cynorra.kpssarena.notification;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ListenableWorker;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Opt-in reminders with a user-chosen frequency:
 * LOW = 09:10 daily test, NORMAL = + 20:00 evening streak/goal nudge, HIGH = + 13:30 midday practice nudge.
 * Every slot only fires when it is relevant (see the workers), so "more" never means "spam".
 */
public final class ReminderScheduler {
    public static final int FREQ_LOW = 1;
    public static final int FREQ_NORMAL = 2;
    public static final int FREQ_HIGH = 3;

    private static final String PREFS = "notification_prefs";
    private static final String KEY_ENABLED = "daily_reminder_enabled";
    private static final String KEY_PROMPTED = "daily_reminder_prompted";
    private static final String KEY_FREQUENCY = "reminder_frequency";
    private static final String WORK_MORNING = "daily_test_reminder"; // kept: existing installs already have this job
    private static final String WORK_NOON = "practice_noon_reminder";
    private static final String WORK_EVENING = "streak_evening_reminder";

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

    public static int getFrequency(Context context) {
        return prefs(context).getInt(KEY_FREQUENCY, FREQ_NORMAL);
    }

    public static void setFrequency(Context context, int frequency) {
        prefs(context).edit().putInt(KEY_FREQUENCY, frequency).apply();
        if (isEnabled(context)) schedule(context);
    }

    public static void setEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply();
        if (enabled) {
            NotificationHelper.ensureChannel(context);
            schedule(context);
        } else {
            WorkManager wm = WorkManager.getInstance(context.getApplicationContext());
            wm.cancelUniqueWork(WORK_MORNING);
            wm.cancelUniqueWork(WORK_NOON);
            wm.cancelUniqueWork(WORK_EVENING);
        }
    }

    /** Keeps the schedule consistent with the saved choice (e.g. after an app update). */
    public static void sync(Context context) {
        if (isEnabled(context)) {
            schedule(context);
        }
    }

    private static void schedule(Context context) {
        WorkManager wm = WorkManager.getInstance(context.getApplicationContext());
        int frequency = getFrequency(context);

        enqueue(wm, WORK_MORNING, DailyReminderWorker.class, 9, 10, null);

        if (frequency >= FREQ_NORMAL) {
            enqueue(wm, WORK_EVENING, SlotReminderWorker.class, 20, 0,
                    new Data.Builder().putString(SlotReminderWorker.KEY_SLOT, SlotReminderWorker.SLOT_EVENING).build());
        } else {
            wm.cancelUniqueWork(WORK_EVENING);
        }
        if (frequency >= FREQ_HIGH) {
            enqueue(wm, WORK_NOON, SlotReminderWorker.class, 13, 30,
                    new Data.Builder().putString(SlotReminderWorker.KEY_SLOT, SlotReminderWorker.SLOT_NOON).build());
        } else {
            wm.cancelUniqueWork(WORK_NOON);
        }
    }

    private static void enqueue(WorkManager wm, String name, Class<? extends ListenableWorker> worker,
                                int hour, int minute, Data input) {
        Calendar next = Calendar.getInstance();
        next.set(Calendar.HOUR_OF_DAY, hour);
        next.set(Calendar.MINUTE, minute);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        if (next.getTimeInMillis() <= System.currentTimeMillis()) {
            next.add(Calendar.DAY_OF_YEAR, 1);
        }
        long delay = next.getTimeInMillis() - System.currentTimeMillis();
        PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest.Builder(worker, 24, TimeUnit.HOURS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build());
        if (input != null) builder.setInputData(input);
        wm.enqueueUniquePeriodicWork(name, ExistingPeriodicWorkPolicy.UPDATE, builder.build());
    }
}
