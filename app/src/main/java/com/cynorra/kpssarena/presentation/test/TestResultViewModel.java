package com.cynorra.kpssarena.presentation.test;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import com.cynorra.kpssarena.domain.model.ScoreResult;
import com.cynorra.kpssarena.domain.model.User;
import com.cynorra.kpssarena.domain.repository.AuthRepository;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import com.google.firebase.auth.FirebaseAuth;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
@HiltViewModel
public class TestResultViewModel extends BaseViewModel {
    private final MutableLiveData<ScoreResult> scoreResult = new MutableLiveData<>();
    private final MutableLiveData<Integer> rank = new MutableLiveData<>();
    private final MutableLiveData<String> shareText = new MutableLiveData<>();
    private final AuthRepository authRepository;
    @Inject
    public TestResultViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }
    public void setScoreResult(ScoreResult result) {
        scoreResult.setValue(result);
        loadUserRank();

    }
    private void loadUserRank() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Rank would be calculated server-side or from leaderboard position
        // Placeholder logic
        ScoreResult result = scoreResult.getValue();
        if (result != null) {
            int estimatedRank = Math.max(1, 100 - result.getTotalScore());
            rank.setValue(estimatedRank);
        }
    }
    public boolean isPremiumUser() {
        User user = authRepository.getCurrentUser();
        return user != null && user.isPremium();
    }
    public LiveData<ScoreResult> getScoreResult() { return scoreResult; }
    public LiveData<Integer> getRank() { return rank; }
    public LiveData<String> getShareText() { return shareText; }
}
