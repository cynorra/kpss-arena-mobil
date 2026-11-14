package com.cynorra.kpssarena.presentation.practice;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cynorra.kpssarena.domain.model.Question;
import com.cynorra.kpssarena.domain.repository.TestRepository;
import com.cynorra.kpssarena.engagement.EngagementStore;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;

/** One practice set = SET_SIZE questions of a topic, with instant feedback and XP after every answer. */
@HiltViewModel
public class PracticeViewModel extends BaseViewModel {
    public static final int SET_SIZE = 10;

    /** Result of answering one question. */
    public static final class AnswerResult {
        public final String selected;
        public final String correct;
        public final boolean isCorrect;
        public final int xpEarned;
        public final boolean goalJustReached;

        AnswerResult(String selected, String correct, boolean isCorrect, int xpEarned, boolean goalJustReached) {
            this.selected = selected;
            this.correct = correct;
            this.isCorrect = isCorrect;
            this.xpEarned = xpEarned;
            this.goalJustReached = goalJustReached;
        }
    }

    private final TestRepository testRepository;
    private final Context context;

    private List<Question> questions = new ArrayList<>();
    private int index = 0;
    private int correctCount = 0;
    private int xpTotal = 0;
    private String category;

    private final MutableLiveData<Question> currentQuestion = new MutableLiveData<>();
    private final MutableLiveData<Integer> position = new MutableLiveData<>(0);
    private final MutableLiveData<AnswerResult> answerResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> finished = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> empty = new MutableLiveData<>(false);

    @Inject
    public PracticeViewModel(TestRepository testRepository, @ApplicationContext Context context) {
        this.testRepository = testRepository;
        this.context = context;
        loadSet(null);
    }

    public void loadSet(String category) {
        this.category = category;
        setLoading(true);
        testRepository.getPracticeQuestions(category, SET_SIZE)
                .addOnSuccessListener(list -> {
                    setLoading(false);
                    questions = list != null ? new ArrayList<>(list) : new ArrayList<>();
                    index = 0;
                    correctCount = 0;
                    xpTotal = 0;
                    finished.setValue(false);
                    answerResult.setValue(null);
                    empty.setValue(questions.isEmpty());
                    position.setValue(0);
                    currentQuestion.setValue(questions.isEmpty() ? null : questions.get(0));
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    questions = new ArrayList<>();
                    empty.setValue(true);
                    currentQuestion.setValue(null);
                });
    }

    public void newSet() {
        loadSet(category);
    }

    public void answer(String selected) {
        Question q = currentQuestion.getValue();
        if (q == null || answerResult.getValue() != null) return; // already answered
        boolean correct = selected.equals(q.getCorrectAnswer());
        boolean goalWasReached = EngagementStore.isGoalReached(context);
        int xp = EngagementStore.recordPracticeAnswer(context, correct);
        boolean goalNow = !goalWasReached && EngagementStore.isGoalReached(context);
        if (correct) correctCount++;
        xpTotal += xp;
        answerResult.setValue(new AnswerResult(selected, q.getCorrectAnswer(), correct, xp, goalNow));
    }

    /** Moves on; after the last question the set is finished. */
    public void next() {
        if (answerResult.getValue() == null) return; // must answer first
        if (index + 1 >= questions.size()) {
            finished.setValue(true);
            return;
        }
        index++;
        answerResult.setValue(null);
        position.setValue(index);
        currentQuestion.setValue(questions.get(index));
    }

    public boolean isLastQuestion() { return index + 1 >= questions.size(); }
    public int getTotal() { return questions.size(); }
    public int getCorrectCount() { return correctCount; }
    public int getXpTotal() { return xpTotal; }

    public LiveData<Question> getCurrentQuestion() { return currentQuestion; }
    public LiveData<Integer> getPosition() { return position; }
    public LiveData<AnswerResult> getAnswerResult() { return answerResult; }
    public LiveData<Boolean> getFinished() { return finished; }
    public LiveData<Boolean> getEmpty() { return empty; }
}
