package com.cynorra.kpssarena.data.remote;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
public class FirebaseRealtimeSource {
    private final FirebaseDatabase database;
    @Inject
    public FirebaseRealtimeSource(FirebaseDatabase database) {
        this.database = database;
    }
    public void listenMatchScore(String matchId, String playerKey, ValueEventListener listener) {
        DatabaseReference ref = database.getReference("matchScores")
                .child(matchId)
                .child(playerKey);
        ref.addValueEventListener(listener);
    }
    public void updateMatchScore(String matchId, String playerKey, int score) {
        DatabaseReference ref = database.getReference("matchScores")
                .child(matchId)
                .child(playerKey);
        ref.setValue(score);
    }
    public void listenMatchStatus(String matchId, ValueEventListener listener) {
        DatabaseReference ref = database.getReference("matchStatus")

                .child(matchId)
                .child("status");
        ref.addValueEventListener(listener);
    }
    public void setMatchStatus(String matchId, String status) {
        DatabaseReference ref = database.getReference("matchStatus")
                .child(matchId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        ref.updateChildren(updates);
    }
    public void removeMatchListeners(String matchId) {
        database.getReference("matchScores").child(matchId).removeEventListener(null);
        database.getReference("matchStatus").child(matchId).removeEventListener(null);
    }
}