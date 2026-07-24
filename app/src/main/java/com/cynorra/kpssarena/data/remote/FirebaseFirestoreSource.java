package com.cynorra.kpssarena.data.remote;
import android.util.Log;
import com.cynorra.kpssarena.data.remote.model.DailyTestDto;
import com.cynorra.kpssarena.data.remote.model.MatchDto;
import com.cynorra.kpssarena.data.remote.model.ScoreDto;
import com.cynorra.kpssarena.data.remote.model.UserDto;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;
@Singleton
public class FirebaseFirestoreSource {

    private final FirebaseFirestore firestore;
    @Inject
    public FirebaseFirestoreSource(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }
    
    public Task<Void> createUserProfile(String uid, UserDto userDto) {
        return firestore.collection("users")
                .document(uid)
                .set(userDto, SetOptions.merge());
    }
    public Task<DocumentSnapshot> getUserProfile(String uid) {
        return firestore.collection("users")
                .document(uid)
                .get();
    }
    public Task<Void> updateUserProfile(String uid, Map<String, Object> updates) {
        return firestore.collection("users")
                .document(uid)
                .update(updates);
    }
    public Task<QuerySnapshot> searchUserByUsername(String username) {
        return firestore.collection("users")
                .whereEqualTo("username", username)
                .get();
    }
    public Task<Void> followUser(String currentUid, String targetUid) {
        WriteBatch batch = firestore.batch();

        DocumentReference currentUserRef = firestore.collection("users").document(currentUid);
        DocumentReference targetUserRef = firestore.collection("users").document(targetUid);

        batch.update(currentUserRef, "following", FieldValue.arrayUnion(targetUid));
        batch.update(targetUserRef, "followers", FieldValue.arrayUnion(currentUid));
        
        return batch.commit();
    }
    public Task<Void> unfollowUser(String currentUid, String targetUid) {
        WriteBatch batch = firestore.batch();
        
        DocumentReference currentUserRef = firestore.collection("users").document(currentUid);
        DocumentReference targetUserRef = firestore.collection("users").document(targetUid);

        batch.update(currentUserRef, "following", FieldValue.arrayRemove(targetUid));
        batch.update(targetUserRef, "followers", FieldValue.arrayRemove(currentUid));
        
        return batch.commit();
    }
    public Task<DocumentSnapshot> getDailyTest(String date) {
        return firestore.collection("dailyTests")
                .document(date)
                .get();
    }
    public Task<Void> createDailyTest(String date, DailyTestDto testDto) {
        return firestore.collection("dailyTests")
                .document(date)
                .set(testDto);
    }
    public Task<Void> submitTestScore(String date, String uid, ScoreDto scoreDto) {
        return firestore.collection("dailyResults")
                .document(date)
                .collection("scores")
                .document(uid)
                .set(scoreDto);

    }
    public Task<DocumentSnapshot> getUserScore(String date, String uid) {
        return firestore.collection("dailyResults")
                .document(date)
                .collection("scores")
                .document(uid)
                .get();
    }
    public Task<QuerySnapshot> getDailyLeaderboard(String date, int limit) {
        return firestore.collection("dailyResults")
                .document(date)
                .collection("scores")
                .orderBy("totalScore", Query.Direction.DESCENDING)
                .limit(limit)
                .get();
    }
    public Task<QuerySnapshot> getWeeklyLeaderboard(String startDate, String endDate) {
        // Aggregate weekly scores (client-side for now, Firestore collection group query)
        return firestore.collectionGroup("scores")
                .whereGreaterThanOrEqualTo("completedAt", startDate)
                .whereLessThanOrEqualTo("completedAt", endDate)
                .orderBy("totalScore", Query.Direction.DESCENDING)
                .limit(100)
                .get();
    }
    public Task<DocumentReference> createMatch(MatchDto matchDto) {
        return firestore.collection("matches")
                .add(matchDto);
    }
    public Task<DocumentSnapshot> getMatch(String matchId) {
        return firestore.collection("matches")
                .document(matchId)
                .get();

    }
    public Task<Void> updateMatch(String matchId, Map<String, Object> updates) {
        return firestore.collection("matches")
                .document(matchId)
                .update(updates);
    }
    public Task<QuerySnapshot> getOpenWaitingMatches(int limit) {
        return firestore.collection("matches")
                .whereEqualTo("status", "WAITING")
                .whereEqualTo("player2Uid", "")
                .limit(limit)
                .get();
    }
    public Task<QuerySnapshot> getUserMatches(String uid) {
        return firestore.collection("matches")
                .whereArrayContains("players", uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(50)
                .get();
    }
    public Task<QuerySnapshot> getUserTestHistory(String uid, int limit) {
        return firestore.collectionGroup("scores")
                .whereEqualTo("uid", uid)
                .orderBy("completedAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get();
    }
    public Task<DocumentSnapshot> getUserClaims(String uid) {
        return firestore.collection("adminUsers")
                .document(uid)
                .get();
    }
    public Task<Void> addAdminClaim(String uid) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("isAdmin", true);
        claims.put("addedAt", FieldValue.serverTimestamp());
        
        return firestore.collection("adminUsers")

                .document(uid)
                .set(claims);
    }
    public Task<Void> awardBadge(String uid, String badgeId) {
        Map<String, Object> badgeData = new HashMap<>();
        badgeData.put("badgeId", badgeId);
        badgeData.put("awardedAt", FieldValue.serverTimestamp());
        
        return firestore.collection("users")
                .document(uid)
                .update("badges", FieldValue.arrayUnion(badgeData));
    }

    public Task<QuerySnapshot> getRandomQuestionsFromPool(int limit) {
        String randomId = java.util.UUID.randomUUID().toString();
        return firestore.collection("QuestionPool")
                .whereGreaterThanOrEqualTo(com.google.firebase.firestore.FieldPath.documentId(), randomId)
                .limit(limit)
                .get()
                .continueWithTask(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().size() == limit) {
                        return Tasks.forResult(task.getResult());
                    }
                    // Fallback to getting from the beginning if random UUID was near the end
                    return firestore.collection("QuestionPool")
                            .limit(limit)
                            .get();
                });
    }
}