package com.cynorra.kpssarena.data.repository;
import com.cynorra.kpssarena.data.local.dao.UserCacheDao;
import com.cynorra.kpssarena.data.remote.FirebaseFirestoreSource;
import com.cynorra.kpssarena.data.remote.model.MatchDto;
import com.cynorra.kpssarena.data.remote.model.ScoreDto;
import com.cynorra.kpssarena.domain.model.StatsData;
import com.cynorra.kpssarena.domain.repository.StatsRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
public class StatsRepositoryImpl implements StatsRepository {
    private final FirebaseFirestoreSource firestoreSource;
    private final UserCacheDao userCacheDao;
    @Inject
    public StatsRepositoryImpl(FirebaseFirestoreSource firestoreSource, UserCacheDao userCacheDao) {
        this.firestoreSource = firestoreSource;
        this.userCacheDao = userCacheDao;
    }
    @Override
    public Task<StatsData> getUserStats(String uid) {
        return firestoreSource.getUserTestHistory(uid, 500)
                .continueWithTask(historyTask -> {
                    List<ScoreDto> scores = new ArrayList<>();
                    if (historyTask.isSuccessful() && historyTask.getResult() != null) {
                        QuerySnapshot snapshot = historyTask.getResult();
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            ScoreDto dto = doc.toObject(ScoreDto.class);
                            if (dto != null) scores.add(dto);
                        }
                    }
                    return firestoreSource.getUserMatches(uid)
                            .continueWith(matchesTask -> {
                                List<MatchDto> matches = new ArrayList<>();
                                if (matchesTask.isSuccessful() && matchesTask.getResult() != null) {
                                    QuerySnapshot snapshot = matchesTask.getResult();
                                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                                        MatchDto dto = doc.toObject(MatchDto.class);
                                        if (dto != null) matches.add(dto);
                                    }
                                }
                                return buildStats(uid, scores, matches);
                            });
                });
    }
    private StatsData buildStats(String uid, List<ScoreDto> scores, List<MatchDto> matches) {
        StatsData stats = new StatsData();
        stats.setTotalTests(scores.size());
        stats.setWeeklyNetAverages(new HashMap<>());
        stats.setCategoryAccuracy(new HashMap<>());
        List<StatsData.DailyScoreEntry> dailyScores = new ArrayList<>();
        int totalScoreSum = 0;
        int highestScore = 0;
        long durationSum = 0;
        int durationCount = 0;
        for (ScoreDto score : scores) {
            totalScoreSum += score.getTotalScore();
            if (score.getTotalScore() > highestScore) {
                highestScore = score.getTotalScore();
            }
            if (score.getDurationSeconds() > 0) {
                durationSum += score.getDurationSeconds();
                durationCount++;
            }
            if (score.getCompletedAt() != null) {
                dailyScores.add(new StatsData.DailyScoreEntry(
                        score.getCompletedAt(), score.getNetScore(), score.getTotalScore()));
            }
        }
        stats.setAverageScore(scores.isEmpty() ? 0 : totalScoreSum / (double) scores.size());
        stats.setHighestScore(highestScore);
        stats.setAverageTimePerTest(durationCount == 0 ? 0 : durationSum / (double) durationCount);
        stats.setDailyScores(dailyScores);
        int matchesWon = 0;
        for (MatchDto match : matches) {
            if (uid.equals(match.getWinnerUid())) {
                matchesWon++;
            }
        }
        stats.setTotalMatchesPlayed(matches.size());
        stats.setTotalMatchesWon(matchesWon);
        stats.setMatchWinRate(matches.isEmpty() ? 0 : matchesWon / (double) matches.size());
        return stats;
    }
}
