package com.cynorra.kpssarena.engagement;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Local engagement state: XP / level, today's practice goal and practice totals.
 * Kept on-device on purpose (no Firestore rules change, works offline); it is a motivation layer,
 * the competitive score stays server-side.
 */
public final class EngagementStore {
    private static final String PREFS = "engagement_prefs";
    private static final String KEY_XP = "xp";
    private static final String KEY_GOAL_DATE = "goal_date";
    private static final String KEY_GOAL_DONE = "goal_done";
    private static final String KEY_TOTAL_ANSWERED = "total_answered";
    private static final String KEY_TOTAL_CORRECT = "total_correct";
    private static final String KEY_LAST_PRACTICE_DATE = "last_practice_date";

    public static final int DAILY_GOAL = 10;
    public static final int XP_CORRECT = 5;
    public static final int XP_WRONG = 1;
    public static final int XP_DAILY_TEST_BASE = 20;
    public static final int XP_DAILY_TEST_PER_CORRECT = 2;
    public static final int XP_GOAL_BONUS = 15;
    private static final int XP_PER_LEVEL = 100;

    private EngagementStore() {}

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private static String today() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    public static int getXp(Context context) {
        return prefs(context).getInt(KEY_XP, 0);
    }

    public static void addXp(Context context, int amount) {
        if (amount <= 0) return;
        SharedPreferences p = prefs(context);
        p.edit().putInt(KEY_XP, p.getInt(KEY_XP, 0) + amount).apply();
    }

    /** Level grows every 100 XP; starts at 1. */
    public static int getLevel(Context context) {
        return getXp(context) / XP_PER_LEVEL + 1;
    }

    public static int getXpIntoLevel(Context context) {
        return getXp(context) % XP_PER_LEVEL;
    }

    public static int getXpPerLevel() {
        return XP_PER_LEVEL;
    }

    /** Questions answered in practice today (resets automatically on a new day). */
    public static int getTodayAnswered(Context context) {
        SharedPreferences p = prefs(context);
        return today().equals(p.getString(KEY_GOAL_DATE, "")) ? p.getInt(KEY_GOAL_DONE, 0) : 0;
    }

    public static boolean isGoalReached(Context context) {
        return getTodayAnswered(context) >= DAILY_GOAL;
    }

    /**
     * Records one practice answer and awards XP.
     *
     * @return XP earned for this answer (includes the one-time daily goal bonus)
     */
    public static int recordPracticeAnswer(Context context, boolean correct) {
        SharedPreferences p = prefs(context);
        String today = today();
        int done = today.equals(p.getString(KEY_GOAL_DATE, "")) ? p.getInt(KEY_GOAL_DONE, 0) : 0;
        int earned = correct ? XP_CORRECT : XP_WRONG;
        boolean reachedNow = done < DAILY_GOAL && done + 1 >= DAILY_GOAL;
        if (reachedNow) earned += XP_GOAL_BONUS;
        p.edit()
                .putString(KEY_GOAL_DATE, today)
                .putInt(KEY_GOAL_DONE, done + 1)
                .putInt(KEY_TOTAL_ANSWERED, p.getInt(KEY_TOTAL_ANSWERED, 0) + 1)
                .putInt(KEY_TOTAL_CORRECT, p.getInt(KEY_TOTAL_CORRECT, 0) + (correct ? 1 : 0))
                .putString(KEY_LAST_PRACTICE_DATE, today)
                .putInt(KEY_XP, p.getInt(KEY_XP, 0) + earned)
                .apply();
        return earned;
    }

    public static boolean practicedToday(Context context) {
        return today().equals(prefs(context).getString(KEY_LAST_PRACTICE_DATE, ""));
    }

    public static int getTotalAnswered(Context context) {
        return prefs(context).getInt(KEY_TOTAL_ANSWERED, 0);
    }

    public static int getTotalCorrect(Context context) {
        return prefs(context).getInt(KEY_TOTAL_CORRECT, 0);
    }
}
