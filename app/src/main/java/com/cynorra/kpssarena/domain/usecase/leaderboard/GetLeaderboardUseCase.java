package com.cynorra.kpssarena.domain.usecase.leaderboard;
import com.cynorra.kpssarena.domain.model.LeaderboardEntry;
import com.cynorra.kpssarena.domain.repository.LeaderboardRepository;
import com.google.android.gms.tasks.Task;
import java.util.List;
import javax.inject.Inject;
public class GetLeaderboardUseCase {
    private final LeaderboardRepository leaderboardRepository;
    @Inject
    public GetLeaderboardUseCase(LeaderboardRepository leaderboardRepository) {
        this.leaderboardRepository = leaderboardRepository;
    }
    public Task<List<LeaderboardEntry>> getToday(int limit) {
        return leaderboardRepository.getToday(limit);
    }
    public Task<List<LeaderboardEntry>> getWeekly(int limit) {
        return leaderboardRepository.getWeekly(limit);
    }
    public Task<List<LeaderboardEntry>> getMonthly(int limit) {
        return leaderboardRepository.getMonthly(limit);
    }
    public Task<List<LeaderboardEntry>> getTodayTop(int limit) {
        return leaderboardRepository.getToday(limit);
    }
}
