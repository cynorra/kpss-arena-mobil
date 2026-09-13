package com.cynorra.kpssarena.presentation.test;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import com.cynorra.kpssarena.domain.model.ScoreResult;
import com.cynorra.kpssarena.domain.model.User;
import com.cynorra.kpssarena.domain.repository.AuthRepository;
import com.cynorra.kpssarena.domain.repository.TestRepository;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import timber.log.Timber;
@HiltViewModel
public class TestResultViewModel extends BaseViewModel {
    private final MutableLiveData<ScoreResult> scoreResult = new MutableLiveData<>();
    private final MutableLiveData<Integer> rank = new MutableLiveData<>();
    private final MutableLiveData<String> shareText = new MutableLiveData<>();
    private final AuthRepository authRepository;
    private final TestRepository testRepository;
    @Inject
    public TestResultViewModel(AuthRepository authRepository, TestRepository testRepository) {
        this.authRepository = authRepository;
        this.testRepository = testRepository;
    }
    public void setScoreResult(ScoreResult result) {
        scoreResult.setValue(result);
        loadUserRank();

    }
    private void loadUserRank() {
        ScoreResult result = scoreResult.getValue();
        if (result == null || result.getCompletedAt() == null) {
            return;
        }
        testRepository.getUserRank(result.getCompletedAt(), result.getTotalScore())
                .addOnSuccessListener(rank::setValue)
                .addOnFailureListener(e -> Timber.e(e, "Sıralama yüklenemedi"));
    }
    public boolean isPremiumUser() {
        User user = authRepository.getCurrentUser();
        return user != null && user.isPremium();
    }
    public LiveData<ScoreResult> getScoreResult() { return scoreResult; }
    public LiveData<Integer> getRank() { return rank; }
    public LiveData<String> getShareText() { return shareText; }
}
