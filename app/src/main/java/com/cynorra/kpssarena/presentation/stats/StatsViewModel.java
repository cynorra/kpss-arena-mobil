package com.cynorra.kpssarena.presentation.stats;
import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.domain.model.StatsData;
import com.cynorra.kpssarena.domain.usecase.stats.GetStatsUseCase;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
@HiltViewModel
public class StatsViewModel extends BaseViewModel {
    private final GetStatsUseCase getStatsUseCase;
    private final Context context;
    private final MutableLiveData<StatsData> statsData = new MutableLiveData<>();
    @Inject
    public StatsViewModel(GetStatsUseCase getStatsUseCase, @ApplicationContext Context context) {
        this.getStatsUseCase = getStatsUseCase;
        this.context = context;
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
                    setError(context.getString(R.string.error_stats_load_failed));
                });
    }
    public LiveData<StatsData> getStatsData() { return statsData; }
}
