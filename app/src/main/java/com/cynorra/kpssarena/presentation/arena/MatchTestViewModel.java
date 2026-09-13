package com.cynorra.kpssarena.presentation.arena;
import android.content.Context;
import android.os.CountDownTimer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.domain.model.Match;
import com.cynorra.kpssarena.domain.model.Question;
import com.cynorra.kpssarena.domain.repository.MatchRepository;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
@HiltViewModel
public class MatchTestViewModel extends BaseViewModel {
    private final MatchRepository matchRepository;
    private final Context context;
    private final MutableLiveData<Question> currentQuestion = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentQuestionIndex = new MutableLiveData<>();
    private final MutableLiveData<Long> timeRemaining = new MutableLiveData<>();
    private final MutableLiveData<Integer> myScore = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> opponentScore = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> navigateToResult = new MutableLiveData<>(false);
    private Match match;
    private String matchId;
    private final String myUid;
    private List<Question> questions = new ArrayList<>();
    private int score = 0;
    private CountDownTimer questionTimer;
    private static final int TIME_PER_QUESTION_MS = 45000;
    @Inject
    public MatchTestViewModel(MatchRepository matchRepository, @ApplicationContext Context context) {
        this.matchRepository = matchRepository;
        this.context = context;
        this.myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    public void loadMatch(String matchId) {
        this.matchId = matchId;
        setLoading(true);
        matchRepository.getMatch(matchId)
                .addOnSuccessListener(m -> {
                    setLoading(false);
                    this.match = m;
                    this.questions = m != null && m.getQuestions() != null ? m.getQuestions() : new ArrayList<>();
                    String opponentUid = m != null ? m.getOpponentUid(myUid) : null;
                    if (opponentUid != null) {
                        matchRepository.listenOpponentScore(matchId, opponentUid, opponentScore::postValue);
                    }
                    if (!questions.isEmpty()) {
                        currentQuestionIndex.setValue(0);
                        currentQuestion.setValue(questions.get(0));
                        startQuestionTimer();
                    }
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError(context.getString(R.string.error_match_load_failed));
                });
    }
    private void startQuestionTimer() {
        if (questionTimer != null) questionTimer.cancel();
        questionTimer = new CountDownTimer(TIME_PER_QUESTION_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining.setValue(millisUntilFinished);
            }
            @Override
            public void onFinish() {
                goToNextOrFinish();
            }
        };
        questionTimer.start();
    }
    public void answerCurrentQuestion(String answer) {
        Integer index = currentQuestionIndex.getValue();
        if (index != null && index < questions.size()) {
            Question q = questions.get(index);
            q.setUserAnswer(answer);
            score += answer.equals(q.getCorrectAnswer()) ? 4 : -1;
            int clamped = Math.max(0, score);
            myScore.setValue(clamped);
            matchRepository.submitAnswer(matchId, myUid, clamped);
        }
    }
    public void nextQuestion() {
        goToNextOrFinish();
    }
    private void goToNextOrFinish() {
        if (questionTimer != null) questionTimer.cancel();
        Integer index = currentQuestionIndex.getValue();
        if (index != null && index < questions.size() - 1) {
            index++;
            currentQuestionIndex.setValue(index);
            currentQuestion.setValue(questions.get(index));
            startQuestionTimer();
        } else {
            finishMatch();
        }
    }
    private void finishMatch() {
        setLoading(true);
        int finalScore = Math.max(0, score);
        matchRepository.completeMatch(matchId, myUid, finalScore)
                .addOnSuccessListener(v -> {
                    setLoading(false);
                    navigateToResult.setValue(true);
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    navigateToResult.setValue(true);
                });
    }
    public int getTotalQuestions() { return questions.size(); }
    public LiveData<Question> getCurrentQuestion() { return currentQuestion; }
    public LiveData<Integer> getCurrentQuestionIndex() { return currentQuestionIndex; }
    public LiveData<Long> getTimeRemaining() { return timeRemaining; }
    public LiveData<Integer> getMyScore() { return myScore; }
    public LiveData<Integer> getOpponentScore() { return opponentScore; }
    public LiveData<Boolean> getNavigateToResult() { return navigateToResult; }
    @Override
    protected void onCleared() {
        super.onCleared();
        if (questionTimer != null) questionTimer.cancel();
        if (matchId != null) matchRepository.stopListening(matchId);
    }
}
