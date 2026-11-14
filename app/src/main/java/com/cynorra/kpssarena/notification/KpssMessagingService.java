package com.cynorra.kpssarena.notification;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Server announcements (new test, events, "your rank dropped"). When the app is in the background the
 * system tray shows notification-payload messages by itself; this handles the foreground case and
 * data-only messages (data keys: title, body).
 */
public class KpssMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        String title = null;
        String body = null;
        if (message.getNotification() != null) {
            title = message.getNotification().getTitle();
            body = message.getNotification().getBody();
        }
        if (title == null) title = message.getData().get("title");
        if (body == null) body = message.getData().get("body");
        if (title == null || body == null) return;
        // Respects the user's notification opt-in: nothing is shown without the permission.
        NotificationHelper.show(getApplicationContext(), NotificationHelper.ID_PUSH, title, body);
    }
}
