package com.cynorra.kpssarena.notification;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.engagement.EngagementStore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Extra daily nudges besides the 09:10 test reminder: a midday "practice" nudge and an evening
 * "protect your streak / finish your goal" nudge. Each one only fires when it is actually relevant.
 */
public class SlotReminderWorker extends Worker {
    public static final String KEY_SLOT = "slot";
    public static final String SLOT_NOON = "noon";
    public static final String SLOT_EVENING = "evening";

    public SlotReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        if (!ReminderScheduler.isEnabled(context) || !NotificationHelper.canNotify(context)) {
            return Result.success();
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return Result.success();

        String slot = getInputData().getString(KEY_SLOT);
        if (SLOT_NOON.equals(slot)) {
            if (!EngagementStore.practicedToday(context)) {
                NotificationHelper.show(context, NotificationHelper.ID_NOON,
                        context.getString(R.string.notif_noon_title),
                        context.getString(R.string.notif_noon_text));
            }
        } else if (SLOT_EVENING.equals(slot)) {
            showEvening(context, user.getUid());
        }
        return Result.success();
    }

    private void showEvening(Context context, String uid) {
        boolean testDone = DailyReminderWorker.hasCompletedToday(uid);
        int answered = EngagementStore.getTodayAnswered(context);
        if (!testDone) {
            int streak = DailyReminderWorker.currentStreak(uid);
            if (streak >= 1) {
                NotificationHelper.show(context, NotificationHelper.ID_EVENING,
                        context.getString(R.string.notif_evening_streak_title),
                        context.getString(R.string.notif_evening_streak_text));
                return;
            }
        }
        if (EngagementStore.isGoalReached(context)) return; // nothing left to nudge about
        if (answered > 0) {
            int left = EngagementStore.DAILY_GOAL - answered;
            NotificationHelper.show(context, NotificationHelper.ID_EVENING,
                    context.getString(R.string.notif_evening_goal_title),
                    context.getString(R.string.notif_evening_goal_text, answered, left));
        } else {
            NotificationHelper.show(context, NotificationHelper.ID_EVENING,
                    context.getString(R.string.notif_evening_start_title),
                    context.getString(R.string.notif_evening_start_text));
        }
    }
}
