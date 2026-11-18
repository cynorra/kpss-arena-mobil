package com.cynorra.kpssarena.presentation.arena;
import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.domain.model.Match;
import com.cynorra.kpssarena.domain.repository.MatchRepository;
import com.cynorra.kpssarena.domain.usecase.match.CreateMatchUseCase;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
@HiltViewModel
public class ArenaViewModel extends BaseViewModel {
    private final CreateMatchUseCase createMatchUseCase;
    private final MatchRepository matchRepository;
    private final Context context;
    private final MutableLiveData<List<Match>> activeMatches = new MutableLiveData<>();
    private final MutableLiveData<List<Match>> openChallenges = new MutableLiveData<>();
    private final MutableLiveData<String> navigateToWaiting = new MutableLiveData<>();
    private final MutableLiveData<List<Match>> matchHistory = new MutableLiveData<>();
    private final MutableLiveData<Match> examDuelCompleted = new MutableLiveData<>();
    private final MutableLiveData<String> duelFeedbackMessage = new MutableLiveData<>();
    @Inject
    public ArenaViewModel(CreateMatchUseCase createMatchUseCase, MatchRepository matchRepository, @ApplicationContext Context context) {
        this.createMatchUseCase = createMatchUseCase;
        this.matchRepository = matchRepository;
        this.context = context;
    }
    public void loadActiveMatches() {
        com.google.firebase.auth.FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;
        matchRepository.getUserActiveMatches(currentUser.getUid())
                .addOnSuccessListener(activeMatches::setValue)
                .addOnFailureListener(e -> setError(context.getString(R.string.error_active_matches_load_failed)));
        loadOpenChallenges();
    }
    public void loadOpenChallenges() {
        matchRepository.getOpenWaitingMatches()
                .addOnSuccessListener(openChallenges::setValue)
                .addOnFailureListener(e -> timber.log.Timber.w(e, "Failed to load open challenges"));
    }
    public void createExamDuel(com.cynorra.kpssarena.domain.model.ExamType examType, com.cynorra.kpssarena.domain.model.ScoreResult result) {
        setLoading(true);
        matchRepository.createExamDuel(examType.name(), result)
                .addOnSuccessListener(matchId -> {
                    setLoading(false);
                    duelFeedbackMessage.setValue("⚔️ " + examType.getTitle() + " düellosu oluşturuldu! Rakip bekleniyor.");
                    loadActiveMatches();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError(e.getMessage() != null ? e.getMessage() : context.getString(R.string.error_match_create_failed));
                });
    }
    public void acceptExamDuel(Match match, com.cynorra.kpssarena.domain.model.ScoreResult myResult) {
        setLoading(true);
        matchRepository.acceptExamDuel(match.getMatchId(), myResult)
                .addOnSuccessListener(completedMatch -> {
                    setLoading(false);
                    examDuelCompleted.setValue(completedMatch);
                    loadActiveMatches();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError(e.getMessage() != null ? e.getMessage() : "Düelloya katılınamadı.");
                });
    }
    public void cancelMatch(String matchId) {
        setLoading(true);
        matchRepository.cancelMatch(matchId)
                .addOnSuccessListener(v -> {
                    setLoading(false);
                    loadActiveMatches();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError("İptal edilemedi.");
                });
    }
    public void onExamDuelShown() {
        examDuelCompleted.setValue(null);
    }
    public void onFeedbackShown() {
        duelFeedbackMessage.setValue(null);
    }
    public void loadMatchHistory() {
        com.google.firebase.auth.FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;
        matchRepository.getUserMatchHistory(currentUser.getUid())
                .addOnSuccessListener(matchHistory::setValue)
                .addOnFailureListener(e -> setError(context.getString(R.string.error_match_history_load_failed)));
    }
    public void onMatchHistoryShown() {
        matchHistory.setValue(null);
    }
    public void resumeMatch(Match match) {
        if (match.getStatus() == Match.MatchStatus.WAITING) {
            navigateToWaiting.setValue(match.getMatchId());
        }
    }
    public void challengeUser(String username) {
        setLoading(true);
        createMatchUseCase.challengeByUsername(username)
                .addOnSuccessListener(matchId -> {
                    setLoading(false);
                    navigateToWaiting.setValue(matchId);
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError(context.getString(R.string.error_user_not_found_or_unavailable));

                });
    }
    public void findRandomMatch() {
        setLoading(true);
        createMatchUseCase.findRandomMatch()
                .addOnSuccessListener(matchId -> {
                    setLoading(false);
                    navigateToWaiting.setValue(matchId);
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError(context.getString(R.string.error_no_suitable_opponent));
                });
    }
    public void onNavigated() {
        navigateToWaiting.setValue(null);
    }
    public LiveData<List<Match>> getActiveMatches() { return activeMatches; }
    public LiveData<List<Match>> getOpenChallenges() { return openChallenges; }
    public LiveData<Match> getExamDuelCompleted() { return examDuelCompleted; }
    public LiveData<String> getDuelFeedbackMessage() { return duelFeedbackMessage; }
    public LiveData<String> getNavigateToWaiting() { return navigateToWaiting; }
    public LiveData<List<Match>> getMatchHistory() { return matchHistory; }
}
