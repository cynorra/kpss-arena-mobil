package com.cynorra.kpssarena.data.repository;
import com.cynorra.kpssarena.data.remote.FirebaseFirestoreSource;
import com.cynorra.kpssarena.data.remote.model.ScoreDto;
import com.cynorra.kpssarena.data.remote.model.UserDto;
import com.cynorra.kpssarena.domain.model.LeaderboardEntry;
import com.cynorra.kpssarena.domain.repository.LeaderboardRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
public class LeaderboardRepositoryImpl implements LeaderboardRepository {
    private final FirebaseFirestoreSource firestoreSource;
    private final FirebaseAnalytics analytics;
    @Inject
    public LeaderboardRepositoryImpl(FirebaseFirestoreSource firestoreSource, FirebaseAnalytics analytics) {
        this.firestoreSource = firestoreSource;
        this.analytics = analytics;
    }
    @Override
    public Task<List<LeaderboardEntry>> getToday(int limit) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return firestoreSource.getDailyLeaderboard(today, limit)
                .continueWithTask(task -> buildEntries(task.getResult(), limit));
    }
    @Override
    public Task<List<LeaderboardEntry>> getWeekly(int limit) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = format.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        String weekAgo = format.format(calendar.getTime());
        return firestoreSource.getWeeklyLeaderboard(weekAgo, today)
                .continueWithTask(task -> buildEntries(task.getResult(), limit));
    }
    @Override
    public Task<List<LeaderboardEntry>> getMonthly(int limit) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = format.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        String monthAgo = format.format(calendar.getTime());
        return firestoreSource.getWeeklyLeaderboard(monthAgo, today)
                .continueWithTask(task -> buildEntries(task.getResult(), limit));
    }
    private Task<List<LeaderboardEntry>> buildEntries(QuerySnapshot snapshot, int limit) {
        List<QueryDocumentSnapshot> docs = new ArrayList<>();
        if (snapshot != null) {
            for (QueryDocumentSnapshot doc : snapshot) {
                docs.add(doc);
                if (docs.size() >= limit) break;
            }
        }
        if (docs.isEmpty()) {
            return Tasks.forResult(new ArrayList<>());
        }
        List<Task<DocumentSnapshot>> profileTasks = new ArrayList<>();
        for (QueryDocumentSnapshot doc : docs) {
            profileTasks.add(firestoreSource.getUserProfile(doc.getId()));
        }
        return Tasks.whenAllComplete(profileTasks)
                .continueWith(whenAllTask -> {
                    Map<String, UserDto> profiles = new HashMap<>();
                    for (Task<DocumentSnapshot> profileTask : profileTasks) {
                        if (profileTask.isSuccessful() && profileTask.getResult() != null && profileTask.getResult().exists()) {
                            DocumentSnapshot doc = profileTask.getResult();
                            profiles.put(doc.getId(), doc.toObject(UserDto.class));
                        }
                    }
                    String currentUid = FirebaseAuth.getInstance().getCurrentUser() != null
                            ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
                    List<LeaderboardEntry> entries = new ArrayList<>();
                    int rank = 1;
                    for (QueryDocumentSnapshot doc : docs) {
                        ScoreDto scoreDto = doc.toObject(ScoreDto.class);
                        UserDto userDto = profiles.get(doc.getId());
                        LeaderboardEntry entry = new LeaderboardEntry(
                                rank,
                                doc.getId(),
                                userDto != null ? userDto.getUsername() : "Kullanıcı",
                                userDto != null ? userDto.getPhotoUrl() : null,
                                scoreDto.getTotalScore(),
                                scoreDto.getNetScore(),
                                scoreDto.getSpeedBonus());
                        entry.setCurrentUser(doc.getId().equals(currentUid));
                        entries.add(entry);
                        rank++;
                    }
                    return entries;
                });
    }
}
