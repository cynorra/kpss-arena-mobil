package com.cynorra.kpssarena.domain.repository;
import com.cynorra.kpssarena.domain.model.StatsData;
import com.google.android.gms.tasks.Task;
public interface StatsRepository {
    Task<StatsData> getUserStats(String uid);
}
