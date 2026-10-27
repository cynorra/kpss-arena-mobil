package com.cynorra.kpssarena.notification;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.cynorra.kpssarena.MainActivity;
import com.cynorra.kpssarena.R;

public final class NotificationHelper {
    public static final String CHANNEL_DAILY_TEST = "daily_test_reminder";
    private static final int NOTIFICATION_ID_DAILY_TEST = 1001;

    private NotificationHelper() {}

    /** Android 13+ needs the runtime POST_NOTIFICATIONS permission; older versions only the channel/app switch. */
    public static boolean canNotify(Context context) {
        if (Build.VERSION.SDK_INT >= 33
                && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    public static void ensureChannel(Context context) {
        if (Build.VERSION.SDK_INT < 26) return;
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager == null) return;
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_DAILY_TEST,
                context.getString(R.string.notif_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(context.getString(R.string.notif_channel_desc));
        manager.createNotificationChannel(channel);
    }

    /** @param streak consecutive days (ending yesterday) the user already solved the daily test */
    public static void showDailyReminder(Context context, int streak) {
        if (!canNotify(context)) return;
        ensureChannel(context);
        String title;
        String text;
        if (streak >= 2) {
            title = context.getString(R.string.notif_streak_title);
            text = context.getResources().getQuantityString(R.plurals.notif_streak_text, streak, streak);
        } else {
            // A few honest variants so the reminder does not read the same every day
            String[] titles = context.getResources().getStringArray(R.array.notif_daily_titles);
            String[] texts = context.getResources().getStringArray(R.array.notif_daily_texts);
            int day = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR);
            int index = day % Math.min(titles.length, texts.length);
            title = titles[index];
            text = texts[index];
        }
        Intent intent = new Intent(context, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_DAILY_TEST)
                .setSmallIcon(R.drawable.ic_sym_notifications)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_DAILY_TEST, builder.build());
        } catch (SecurityException ignored) {
            // Permission revoked between the check and the call - nothing to show.
        }
    }
}
