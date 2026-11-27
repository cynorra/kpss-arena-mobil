package com.cynorra.kpssarena.presentation.mockexam;

import android.content.Context;
import android.os.CountDownTimer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.domain.model.ExamType;
import com.cynorra.kpssarena.domain.model.Mistake;
import com.cynorra.kpssarena.domain.model.Question;
import com.cynorra.kpssarena.domain.model.ScoreResult;
import com.cynorra.kpssarena.domain.repository.MistakeRepository;
import com.cynorra.kpssarena.domain.repository.TestRepository;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;

@HiltViewModel
public class MockExamViewModel extends BaseViewModel {

    private final TestRepository testRepository;
    private final MistakeRepository mistakeRepository;
    private final Context context;

    private ExamType examType;
    private int testNumber = 1;
    private long totalExamDurationMs;

    private final MutableLiveData<ExamType> examTypeLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> examTitleLiveData = new MutableLiveData<>();
    private final MutableLiveData<Question> currentQuestion = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentQuestionIndex = new MutableLiveData<>();
    private final MutableLiveData<Long> timeRemaining = new MutableLiveData<>();
    private final MutableLiveData<ScoreResult> navigateToResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> timeUp = new MutableLiveData<>(false);
    private final MutableLiveData<List<String>> eliminatedOptions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Question>> allQuestionsLiveData = new MutableLiveData<>(new ArrayList<>());

    private List<Question> questions = new ArrayList<>();
    private CountDownTimer totalTimer;
    private int totalTimeUsed = 0;

    @Inject
    public MockExamViewModel(TestRepository testRepository,
                             MistakeRepository mistakeRepository,
                             @ApplicationContext Context context,
                             SavedStateHandle savedStateHandle) {
        this.testRepository = testRepository;
        this.mistakeRepository = mistakeRepository;
        this.context = context;

        String typeArg = savedStateHandle != null ? savedStateHandle.get("examType") : null;
        int testNumArg = 1;
        if (savedStateHandle != null && savedStateHandle.get("testNumber") != null) {
            Object val = savedStateHandle.get("testNumber");
            if (val instanceof Integer) {
                testNumArg = (Integer) val;
            } else if (val instanceof String) {
                try {
                    testNumArg = Integer.parseInt((String) val);
                } catch (Exception ignored) {}
            }
        }
        this.examType = ExamType.fromString(typeArg);
        this.testNumber = Math.max(1, testNumArg);
        this.totalExamDurationMs = this.examType.getDurationMs();
        this.examTypeLiveData.setValue(this.examType);
        this.examTitleLiveData.setValue(getExamTitle());

        loadExam();
    }

    private void loadExam() {
        setLoading(true);
        testRepository.getExamQuestions(examType, testNumber)
                .addOnSuccessListener(list -> {
                    setLoading(false);
                    this.questions = list != null ? list : new ArrayList<>();
                    if (questions.isEmpty()) {
                        setError(context.getString(R.string.mock_exam_empty_pool));
                        return;
                    }
                    allQuestionsLiveData.setValue(this.questions);
                    currentQuestionIndex.setValue(0);
                    currentQuestion.setValue(questions.get(0));
                    eliminatedOptions.setValue(new ArrayList<>());
                    startTotalTimer();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError(context.getString(R.string.error_test_load_failed));
                });
    }

    private void startTotalTimer() {
        if (totalTimer != null) totalTimer.cancel();
        totalTimer = new CountDownTimer(totalExamDurationMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining.setValue(millisUntilFinished);
                totalTimeUsed = (int) (totalExamDurationMs - millisUntilFinished);
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
            allQuestionsLiveData.setValue(questions);
        }
    }

    public void toggleFlagCurrentQuestion() {
        Integer index = currentQuestionIndex.getValue();
        if (index != null && index < questions.size()) {
            Question q = questions.get(index);
            q.setFlagged(!q.isFlagged());
            currentQuestion.setValue(q);
            allQuestionsLiveData.setValue(questions);
        }
    }

    public List<Question> getAllQuestions() {
        return questions;
    }

    public void nextQuestion() {
        Integer index = currentQuestionIndex.getValue();
        if (index != null && index < questions.size() - 1) {
            goToQuestion(index + 1);
        }
    }

    public void previousQuestion() {
        Integer index = currentQuestionIndex.getValue();
        if (index != null && index > 0) {
            goToQuestion(index - 1);
        }
    }

    public void goToQuestion(int index) {
        if (index >= 0 && index < questions.size()) {
            currentQuestionIndex.setValue(index);
            currentQuestion.setValue(questions.get(index));
            eliminatedOptions.setValue(new ArrayList<>());
        }
    }

    public void applyFiftyFiftyJoker() {
        Question q = currentQuestion.getValue();
        if (q == null) return;
        String correct = q.getCorrectAnswer();
        if (correct == null) return;

        List<String> wrong = new ArrayList<>();
        for (String opt : new String[]{"A", "B", "C", "D"}) {
            if (!opt.equalsIgnoreCase(correct)) {
                wrong.add(opt);
            }
        }
        java.util.Collections.shuffle(wrong);
        List<String> toEliminate = new ArrayList<>();
        if (wrong.size() >= 2) {
            toEliminate.add(wrong.get(0));
            toEliminate.add(wrong.get(1));
        } else {
            toEliminate.addAll(wrong);
        }
        eliminatedOptions.setValue(toEliminate);
    }

    public void submitExam() {
        if (totalTimer != null) totalTimer.cancel();
        setLoading(true);
        saveMistakes(questions);

        ScoreResult result = testRepository.calculateScore(questions, totalTimeUsed, (int) totalExamDurationMs);
        result.setCompletedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        result.setQuestions(questions);
        if (examType != null) {
            result.setExamType(examType.name());
            result.setExamTitle(getExamTitle());
        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            testRepository.submitMockExam(uid, examType, testNumber, questions, totalTimeUsed, (int) totalExamDurationMs)
                    .addOnCompleteListener(task -> {
                        setLoading(false);
                        navigateToResult.setValue(result);
                    });
        } else {
            setLoading(false);
            navigateToResult.setValue(result);
        }
    }

    private void saveMistakes(List<Question> questionList) {
        if (questionList == null || questionList.isEmpty()) return;
        List<Mistake> mistakes = new ArrayList<>();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        for (Question q : questionList) {
            if (!q.isCorrect()) {
                Mistake m = new Mistake();
                m.setQuestionId(q.getId() != null ? q.getId() : java.util.UUID.randomUUID().toString());
                m.setQuestionText(q.getText());
                m.setOptionA(q.getOptionA());
                m.setOptionB(q.getOptionB());
                m.setOptionC(q.getOptionC());
                m.setOptionD(q.getOptionD());
                m.setCorrectAnswer(q.getCorrectAnswer());
                m.setUserAnswer(q.getUserAnswer());
                m.setCategory(q.getCategory() != null ? q.getCategory() : "Genel");
                m.setExplanation(q.getExplanation() != null ? q.getExplanation() : "");
                m.setTestDate(today);
                m.setRetried(false);
                m.setRetryCorrect(false);
                m.setCreatedAt(System.currentTimeMillis());
                mistakes.add(m);
            }
        }
        if (!mistakes.isEmpty()) {
            mistakeRepository.insertMistakes(mistakes);
        }
    }

    public int getTotalQuestions() { return questions.size(); }
    public ExamType getExamType() { return examType; }
    public int getTestNumber() { return testNumber; }
    public String getExamTitle() {
        return examType != null ? (examType.getTitle() + (testNumber > 0 ? " #" + testNumber : "")) : "";
    }
    public LiveData<ExamType> getExamTypeLiveData() { return examTypeLiveData; }
    public LiveData<String> getExamTitleLiveData() { return examTitleLiveData; }
    public LiveData<Question> getCurrentQuestion() { return currentQuestion; }
    public LiveData<Integer> getCurrentQuestionIndex() { return currentQuestionIndex; }
    public LiveData<Long> getTimeRemaining() { return timeRemaining; }
    public LiveData<ScoreResult> getNavigateToResult() { return navigateToResult; }
    public LiveData<Boolean> getTimeUp() { return timeUp; }
    public LiveData<List<String>> getEliminatedOptions() { return eliminatedOptions; }
    public LiveData<List<Question>> getAllQuestionsLiveData() { return allQuestionsLiveData; }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (totalTimer != null) totalTimer.cancel();
    }
}
