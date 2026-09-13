package com.cynorra.kpssarena.presentation.mockexam;
import android.content.Context;
import android.os.CountDownTimer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.domain.model.Question;
import com.cynorra.kpssarena.domain.model.ScoreResult;
import com.cynorra.kpssarena.domain.repository.TestRepository;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
@HiltViewModel
public class MockExamViewModel extends BaseViewModel {
    private static final int TOTAL_TIME_MS = 145 * 60 * 1000; // gercek KPSS Genel Yetenek-Genel Kultur suresi
    private final TestRepository testRepository;
    private final Context context;
    private final MutableLiveData<Question> currentQuestion = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentQuestionIndex = new MutableLiveData<>();
    private final MutableLiveData<Long> timeRemaining = new MutableLiveData<>();
    private final MutableLiveData<ScoreResult> navigateToResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> timeUp = new MutableLiveData<>(false);
    private List<Question> questions = new ArrayList<>();
    private CountDownTimer totalTimer;
    private int totalTimeUsed = 0;
    @Inject
    public MockExamViewModel(TestRepository testRepository, @ApplicationContext Context context) {
        this.testRepository = testRepository;
        this.context = context;
        loadExam();
    }
    private void loadExam() {
        setLoading(true);
        testRepository.getMockExamQuestions()
                .addOnSuccessListener(list -> {
                    setLoading(false);
                    this.questions = list != null ? list : new ArrayList<>();
                    if (questions.isEmpty()) {
                        setError(context.getString(R.string.mock_exam_empty_pool));
                        return;
                    }
                    currentQuestionIndex.setValue(0);
                    currentQuestion.setValue(questions.get(0));
                    startTotalTimer();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError(context.getString(R.string.error_test_load_failed));
                });
    }
    private void startTotalTimer() {
        totalTimer = new CountDownTimer(TOTAL_TIME_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining.setValue(millisUntilFinished);
                totalTimeUsed = TOTAL_TIME_MS - (int) millisUntilFinished;
            }
            @Override
            public void onFinish() {
                timeUp.setValue(true);
            }
        };
        totalTimer.start();
    }
    public void answerCurrentQuestion(String answer) {
        Integer index = currentQuestionIndex.getValue();
        if (index != null && index < questions.size()) {
            questions.get(index).setUserAnswer(answer);
        }
    }
    public void nextQuestion() {
        Integer index = currentQuestionIndex.getValue();
        if (index != null && index < questions.size() - 1) {
            index++;
            currentQuestionIndex.setValue(index);
            currentQuestion.setValue(questions.get(index));
        }
    }
    public void previousQuestion() {
        Integer index = currentQuestionIndex.getValue();
        if (index != null && index > 0) {
            index--;
            currentQuestionIndex.setValue(index);
            currentQuestion.setValue(questions.get(index));
        }
    }
    public void submitExam() {
        if (totalTimer != null) totalTimer.cancel();
        setLoading(true);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        testRepository.submitMockExam(uid, questions, totalTimeUsed, TOTAL_TIME_MS)
                .addOnSuccessListener(result -> {
                    setLoading(false);
                    navigateToResult.setValue(result);
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError(context.getString(R.string.error_score_submit_failed));
                });
    }
    public int getTotalQuestions() { return questions.size(); }
    public LiveData<Question> getCurrentQuestion() { return currentQuestion; }
    public LiveData<Integer> getCurrentQuestionIndex() { return currentQuestionIndex; }
    public LiveData<Long> getTimeRemaining() { return timeRemaining; }
    public LiveData<ScoreResult> getNavigateToResult() { return navigateToResult; }
    public LiveData<Boolean> getTimeUp() { return timeUp; }
    @Override
    protected void onCleared() {
        super.onCleared();
        if (totalTimer != null) totalTimer.cancel();
    }
}
