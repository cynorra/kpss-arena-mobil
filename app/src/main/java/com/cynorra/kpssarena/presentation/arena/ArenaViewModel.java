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
    private final MutableLiveData<String> navigateToWaiting = new MutableLiveData<>();
    private final MutableLiveData<List<Match>> matchHistory = new MutableLiveData<>();
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
    public LiveData<String> getNavigateToWaiting() { return navigateToWaiting; }
    public LiveData<List<Match>> getMatchHistory() { return matchHistory; }
}
