package com.cynorra.kpssarena.presentation.stats;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.domain.model.StatsData;
import com.cynorra.kpssarena.domain.usecase.stats.GetStatsUseCase;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
@HiltViewModel
public class StatsViewModel extends BaseViewModel {
    private final GetStatsUseCase getStatsUseCase;
    private final MutableLiveData<StatsData> statsData = new MutableLiveData<>();
    @Inject
    public StatsViewModel(GetStatsUseCase getStatsUseCase) {
        this.getStatsUseCase = getStatsUseCase;
        loadStats();
    }

    private void loadStats() {
        setLoading(true);
        getStatsUseCase.execute()
                .addOnSuccessListener(stats -> {
                    setLoading(false);
                    statsData.setValue(stats);
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError("İstatistikler yüklenemedi.");
                });
    }
    public LiveData<StatsData> getStatsData() { return statsData; }
}
