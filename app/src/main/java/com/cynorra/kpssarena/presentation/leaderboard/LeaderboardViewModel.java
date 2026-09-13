package com.cynorra.kpssarena.presentation.leaderboard;
import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.domain.model.LeaderboardEntry;
import com.cynorra.kpssarena.domain.usecase.leaderboard.GetLeaderboardUseCase;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
@HiltViewModel
public class LeaderboardViewModel extends BaseViewModel {
    private final GetLeaderboardUseCase getLeaderboardUseCase;
    private final Context context;
    private final MutableLiveData<List<LeaderboardEntry>> leaderboardEntries = new MutableLiveData<>();
    private final MutableLiveData<LeaderboardEntry> currentUserEntry = new MutableLiveData<>();
    private int currentFilter = 0; // 0: Today, 1: Weekly, 2: Monthly
    @Inject
    public LeaderboardViewModel(GetLeaderboardUseCase getLeaderboardUseCase, @ApplicationContext Context context) {
        this.getLeaderboardUseCase = getLeaderboardUseCase;
        this.context = context;
        loadLeaderboard();
    }
    public void setFilter(int filter) {
        this.currentFilter = filter;
        loadLeaderboard();
    }
    public void loadLeaderboard() {
        setLoading(true);
        switch (currentFilter) {

            case 0:
                getLeaderboardUseCase.getToday(100)
                        .addOnSuccessListener(this::processEntries)
                        .addOnFailureListener(e -> setError(context.getString(R.string.error_leaderboard_load_failed)));
                break;
            case 1:
                getLeaderboardUseCase.getWeekly(100)
                        .addOnSuccessListener(this::processEntries)
                        .addOnFailureListener(e -> setError(context.getString(R.string.error_leaderboard_load_failed)));
                break;
            case 2:
                getLeaderboardUseCase.getMonthly(100)
                        .addOnSuccessListener(this::processEntries)
                        .addOnFailureListener(e -> setError(context.getString(R.string.error_leaderboard_load_failed)));
                break;
        }
    }
    private void processEntries(List<LeaderboardEntry> entries) {
        setLoading(false);
        leaderboardEntries.postValue(entries);
        // Find current user entry
        for (LeaderboardEntry entry : entries) {
            if (entry.isCurrentUser()) {
                currentUserEntry.postValue(entry);
                break;
            }
        }
    }
    public void refreshLeaderboard() {
        loadLeaderboard();
    }
    public LiveData<List<LeaderboardEntry>> getLeaderboardEntries() { return leaderboardEntries; }
    public LiveData<LeaderboardEntry> getCurrentUserEntry() { return currentUserEntry; }
}
