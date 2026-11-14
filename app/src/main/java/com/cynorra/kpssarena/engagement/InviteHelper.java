package com.cynorra.kpssarena.engagement;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.cynorra.kpssarena.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Personal invite code + Play install-referrer attribution ("who brought this user"). */
public final class InviteHelper {
    private static final String PREFS = "invite_prefs";
    private static final String KEY_REFERRER_CHECKED = "referrer_checked";
    private static final String KEY_PENDING_REFERRER = "pending_referrer";
    private static final String TAG_PREFIX = "invite_";

    private InviteHelper() {}

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    /** Short, stable code derived from the uid. Used only as an attribution tag. */
    public static String myCode() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getUid().length() < 8) return "KPSS";
        return user.getUid().substring(0, 8).toUpperCase(Locale.ROOT);
    }

    /** Play Store link carrying the invite code so the install referrer can read it back. */
    public static String inviteLink(Context context) {
        return context.getString(R.string.play_store_url) + "&referrer=" + TAG_PREFIX + myCode();
    }

    /** Reads the install referrer once per install (cheap, async) and stores a pending invite code. */
    public static void captureInstallReferrer(Context context) {
        final Context app = context.getApplicationContext();
        if (prefs(app).getBoolean(KEY_REFERRER_CHECKED, false)) return;
        try {
            final InstallReferrerClient client = InstallReferrerClient.newBuilder(app).build();
            client.startConnection(new InstallReferrerStateListener() {
                @Override
                public void onInstallReferrerSetupFinished(int responseCode) {
                    try {
                        if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                            String referrer = client.getInstallReferrer().getInstallReferrer();
                            if (referrer != null && referrer.contains(TAG_PREFIX)) {
                                String code = referrer.substring(referrer.indexOf(TAG_PREFIX) + TAG_PREFIX.length());
                                int amp = code.indexOf('&');
                                if (amp >= 0) code = code.substring(0, amp);
                                if (!code.isEmpty()) {
                                    prefs(app).edit().putString(KEY_PENDING_REFERRER, code).apply();
                                }
                            }
                            prefs(app).edit().putBoolean(KEY_REFERRER_CHECKED, true).apply();
                        } else if (responseCode == InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED) {
                            prefs(app).edit().putBoolean(KEY_REFERRER_CHECKED, true).apply();
                        }
                    } catch (Exception ignored) {
                        // Best effort only; retry on the next launch.
                    } finally {
                        try { client.endConnection(); } catch (Exception ignored) { }
                    }
                }

                @Override
                public void onInstallReferrerServiceDisconnected() { }
            });
        } catch (Exception ignored) {
            // Best effort only.
        }
    }

    /** Writes the pending invite code onto the signed-in user's profile, once. */
    public static void flushPendingReferrer(Context context) {
        final Context app = context.getApplicationContext();
        String code = prefs(app).getString(KEY_PENDING_REFERRER, null);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (code == null || user == null) return;
        if (code.equals(myCode())) { // self-invite: ignore
            prefs(app).edit().remove(KEY_PENDING_REFERRER).apply();
            return;
        }
        Map<String, Object> update = new HashMap<>();
        update.put("referredBy", code);
        FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                .update(update)
                .addOnSuccessListener(v -> prefs(app).edit().remove(KEY_PENDING_REFERRER).apply());
    }
}
