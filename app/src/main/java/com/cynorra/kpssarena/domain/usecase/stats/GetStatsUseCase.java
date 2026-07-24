package com.cynorra.kpssarena.domain.usecase.stats;
import com.cynorra.kpssarena.domain.model.StatsData;
import com.cynorra.kpssarena.domain.repository.StatsRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import javax.inject.Inject;
public class GetStatsUseCase {
    private final StatsRepository statsRepository;
    @Inject
    public GetStatsUseCase(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }
    public Task<StatsData> execute() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return statsRepository.getUserStats(uid);
    }
}
