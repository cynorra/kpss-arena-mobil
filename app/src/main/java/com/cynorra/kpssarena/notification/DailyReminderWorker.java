package com.cynorra.kpssarena.notification;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
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
            NotificationHelper.showDailyReminder(context);
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
}
