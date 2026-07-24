package com.cynorra.kpssarena.domain.repository;
import com.cynorra.kpssarena.domain.model.LeaderboardEntry;
import com.google.android.gms.tasks.Task;
import java.util.List;
public interface LeaderboardRepository {
    Task<List<LeaderboardEntry>> getToday(int limit);
    Task<List<LeaderboardEntry>> getWeekly(int limit);
    Task<List<LeaderboardEntry>> getMonthly(int limit);
}
