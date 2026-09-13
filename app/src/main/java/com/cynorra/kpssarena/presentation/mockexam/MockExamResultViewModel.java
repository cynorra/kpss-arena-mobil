package com.cynorra.kpssarena.presentation.mockexam;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.domain.model.ScoreResult;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
@HiltViewModel
public class MockExamResultViewModel extends BaseViewModel {
    private final MutableLiveData<ScoreResult> scoreResult = new MutableLiveData<>();
    @Inject
    public MockExamResultViewModel() {}
    public void setScoreResult(ScoreResult result) {
        scoreResult.setValue(result);
    }
    public LiveData<ScoreResult> getScoreResult() { return scoreResult; }
}
