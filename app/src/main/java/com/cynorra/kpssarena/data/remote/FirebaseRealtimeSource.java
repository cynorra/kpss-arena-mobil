package com.cynorra.kpssarena.data.remote;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
public class FirebaseRealtimeSource {
    private final FirebaseDatabase database;
    private final Map<String, ValueEventListener> scoreListeners = new ConcurrentHashMap<>();
    private final Map<String, ValueEventListener> statusListeners = new ConcurrentHashMap<>();
    @Inject
    public FirebaseRealtimeSource(FirebaseDatabase database) {
        this.database = database;
    }
    public void listenMatchScore(String matchId, String playerKey, ValueEventListener listener) {
        DatabaseReference ref = database.getReference("matchScores")
                .child(matchId)
                .child(playerKey);
        ref.addValueEventListener(listener);
        scoreListeners.put(matchId, listener);
    }
    public Task<Void> updateMatchScore(String matchId, String playerKey, int score) {
        DatabaseReference ref = database.getReference("matchScores")
                .child(matchId)
                .child(playerKey);
        return ref.setValue(score);
    }
    public void listenMatchStatus(String matchId, ValueEventListener listener) {
        DatabaseReference ref = database.getReference("matchStatus")
                .child(matchId)
                .child("status");
        ref.addValueEventListener(listener);
        statusListeners.put(matchId, listener);
    }
    public Task<Void> initMatchParticipants(String matchId, String player1Uid, String player2Uid, String status) {
        DatabaseReference ref = database.getReference("matchStatus").child(matchId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("player1Uid", player1Uid);
        updates.put("player2Uid", player2Uid != null ? player2Uid : "");
        updates.put("status", status);
        return ref.updateChildren(updates);
    }
    public Task<Void> setMatchStatus(String matchId, String status) {
        DatabaseReference ref = database.getReference("matchStatus")
                .child(matchId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        return ref.updateChildren(updates);
    }
    public Task<Void> joinMatchAsPlayer2(String matchId, String player2Uid) {
        DatabaseReference ref = database.getReference("matchStatus").child(matchId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("player2Uid", player2Uid);
        updates.put("status", "READY");
        return ref.updateChildren(updates);
    }
    public void removeMatchListeners(String matchId) {
        ValueEventListener scoreListener = scoreListeners.remove(matchId);
        if (scoreListener != null) {
            database.getReference("matchScores").child(matchId).removeEventListener(scoreListener);
        }
        ValueEventListener statusListener = statusListeners.remove(matchId);
        if (statusListener != null) {
            database.getReference("matchStatus").child(matchId).child("status").removeEventListener(statusListener);
        }
    }
}
