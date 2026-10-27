package com.cynorra.kpssarena.notification;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.cynorra.kpssarena.data.repository.StatsRepositoryImpl;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DailyReminderWorker extends Worker {

    public DailyReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
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
        if (user == null) {
            return Result.success(); // signed out: nothing personal to remind about
        }
        if (!hasCompletedToday(user.getUid())) {
            NotificationHelper.showDailyReminder(context, currentStreak(user.getUid()));
        }
        return Result.success();
    }

    /** Fails open: if we cannot tell (offline, error), the reminder is still shown. */
    private boolean hasCompletedToday(String uid) {
        try {
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            DocumentSnapshot snapshot = Tasks.await(
                    FirebaseFirestore.getInstance()
                            .collection("dailyResults").document(today)
                            .collection("scores").document(uid).get(),
                    15, TimeUnit.SECONDS);
            return snapshot != null && snapshot.exists();
        } catch (Exception e) {
            return false;
        }
    }

    /** Consecutive days with a completed test, counted back from yesterday (today is not done yet). */
    private int currentStreak(String uid) {
        try {
            QuerySnapshot history = Tasks.await(
                    FirebaseFirestore.getInstance()
                            .collectionGroup("scores")
                            .whereEqualTo("uid", uid)
                            .orderBy("completedAt", Query.Direction.DESCENDING)
                            .limit(60)
                            .get(),
                    15, TimeUnit.SECONDS);
            Set<String> days = new HashSet<>();
            for (DocumentSnapshot doc : history.getDocuments()) {
                String completedAt = doc.getString("completedAt");
                if (completedAt != null && completedAt.length() >= 10) {
                    days.add(completedAt.substring(0, 10));
                }
            }
            return StatsRepositoryImpl.currentStreakFrom(days);
        } catch (Exception e) {
            return 0;
        }
    }
}
