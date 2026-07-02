package com.cynorra.kpssarena.presentation.leaderboard;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.domain.model.LeaderboardEntry;
import com.cynorra.kpssarena.domain.usecase.leaderboard.GetLeaderboardUseCase;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
@HiltViewModel
public class LeaderboardViewModel extends BaseViewModel {
    private final GetLeaderboardUseCase getLeaderboardUseCase;
    private final MutableLiveData<List<LeaderboardEntry>> leaderboardEntries = new Mu
    private final MutableLiveData<LeaderboardEntry> currentUserEntry = new MutableLiv
    private int currentFilter = 0; // 0: Today, 1: Weekly, 2: Monthly
    @Inject
    public LeaderboardViewModel(GetLeaderboardUseCase getLeaderboardUseCase) {
        this.getLeaderboardUseCase = getLeaderboardUseCase;
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
                        .addOnFailureListener(e -> setError("Veriler yüklenemedi."));
                break;
            case 1:
                getLeaderboardUseCase.getWeekly(100)
                        .addOnSuccessListener(this::processEntries)
                        .addOnFailureListener(e -> setError("Veriler yüklenemedi."));
                break;
            case 2:
                getLeaderboardUseCase.getMonthly(100)
                        .addOnSuccessListener(this::processEntries)
                        .addOnFailureListener(e -> setError("Veriler yüklenemedi."));
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
    public LiveData<List<LeaderboardEntry>> getLeaderboardEntries() { return leaderboa
    public LiveData<LeaderboardEntry> getCurrentUserEntry() { return currentUserEntry;
}
