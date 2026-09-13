package com.cynorra.kpssarena.presentation.test;
import android.content.Context;
import android.os.CountDownTimer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.domain.model.DailyTest;

import com.cynorra.kpssarena.domain.model.Question;
import com.cynorra.kpssarena.domain.model.ScoreResult;
import com.cynorra.kpssarena.domain.usecase.test.GetDailyTestUseCase;
import com.cynorra.kpssarena.domain.usecase.test.SubmitTestUseCase;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import com.google.firebase.auth.FirebaseAuth;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
@HiltViewModel
public class TestViewModel extends BaseViewModel {
    private final GetDailyTestUseCase getDailyTestUseCase;
    private final SubmitTestUseCase submitTestUseCase;
    private final Context context;
    private final MutableLiveData<Question> currentQuestion = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentQuestionIndex = new MutableLiveData<>();
    private final MutableLiveData<Long> timeRemaining = new MutableLiveData<>();
    private final MutableLiveData<ScoreResult> navigateToResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> timeUp = new MutableLiveData<>(false);
    private DailyTest dailyTest;
    private List<Question> questions = new ArrayList<>();
    private Map<String, String> answers = new HashMap<>();
    private CountDownTimer questionTimer;
    private CountDownTimer totalTimer;
    private int totalTimeUsed = 0;
    private long questionStartTime;
    private static final int TIME_PER_QUESTION_MS = 45000;
    private static final int TOTAL_TIME_MS = 12 * 45000;

    @Inject
    public TestViewModel(GetDailyTestUseCase getDailyTestUseCase,
                         SubmitTestUseCase submitTestUseCase,
                         @ApplicationContext Context context) {
        this.getDailyTestUseCase = getDailyTestUseCase;
        this.submitTestUseCase = submitTestUseCase;
        this.context = context;
        loadTest();
    }
    private void loadTest() {
        setLoading(true);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        getDailyTestUseCase.execute(today)
                .addOnSuccessListener(test -> {
                    setLoading(false);
                    this.dailyTest = test;
                    this.questions = test.getQuestions() != null ? test.getQuestions() : new ArrayList<>();
                    if (!questions.isEmpty()) {
                        currentQuestionIndex.setValue(0);
                        currentQuestion.setValue(questions.get(0));
                        startTotalTimer();
                        startQuestionTimer();
                    }
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
        questionStartTime = System.currentTimeMillis();
    }
    private void startQuestionTimer() {
        if (questionTimer != null) questionTimer.cancel();
    }
    public void answerCurrentQuestion(String answer) {
        Integer index = currentQuestionIndex.getValue();
        if (index != null && index < questions.size()) {
            Question q = questions.get(index);
            q.setUserAnswer(answer);
            answers.put("q" + index, answer);
        }
    }
    public void nextQuestion() {
        Integer index = currentQuestionIndex.getValue();
        if (index != null && index < questions.size() - 1) {
            index++;
            currentQuestionIndex.setValue(index);
            currentQuestion.setValue(questions.get(index));
            startQuestionTimer();
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
    public void submitTest() {

        if (totalTimer != null) totalTimer.cancel();
        if (questionTimer != null) questionTimer.cancel();
        setLoading(true);
        ScoreResult result = submitTestUseCase.calculateScore(questions, totalTimeUsed, TOTAL_TIME_MS);
        result.setCompletedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        result.setQuestions(questions);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        submitTestUseCase.execute(uid, dailyTest, questions)
                .addOnSuccessListener(savedResult -> {
                    setLoading(false);
                    navigateToResult.setValue(savedResult != null ? savedResult : result);
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    navigateToResult.setValue(result);
                });
    }
    public int getTotalQuestions() {
        return questions.size();
    }
    public LiveData<Question> getCurrentQuestion() { return currentQuestion; }
    public LiveData<Integer> getCurrentQuestionIndex() { return currentQuestionIndex; }
    public LiveData<Long> getTimeRemaining() { return timeRemaining; }
    public LiveData<ScoreResult> getNavigateToResult() { return navigateToResult; }
    public LiveData<Boolean> getTimeUp() { return timeUp; }
    @Override
    protected void onCleared() {
        super.onCleared();
        if (totalTimer != null) totalTimer.cancel();
        if (questionTimer != null) questionTimer.cancel();
    }
}
