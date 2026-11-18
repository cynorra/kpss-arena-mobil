package com.cynorra.kpssarena.presentation.arena;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.domain.repository.MatchRepository;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
@HiltViewModel
public class MatchWaitingViewModel extends BaseViewModel {
    private static final long TIMEOUT_MS = 60_000L;
    private final MatchRepository matchRepository;
    private final Context context;
    private final MutableLiveData<Boolean> opponentReady = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> timedOut = new MutableLiveData<>(false);
    private final MutableLiveData<com.cynorra.kpssarena.domain.model.Match> matchLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> opponentJoinedName = new MutableLiveData<>();
    private final Handler timeoutHandler = new Handler(Looper.getMainLooper());
    private final Runnable timeoutRunnable = this::onTimeout;
    private String matchId;
    @Inject
    public MatchWaitingViewModel(MatchRepository matchRepository, @ApplicationContext Context context) {
        this.matchRepository = matchRepository;
        this.context = context;
    }
    public void startWatching(String matchId) {
        this.matchId = matchId;
        timedOut.setValue(false);
        timeoutHandler.postDelayed(timeoutRunnable, TIMEOUT_MS);

        matchRepository.getMatch(matchId).addOnSuccessListener(match -> {
            if (match != null) {
                matchLiveData.setValue(match);
            }
        });

        matchRepository.listenForOpponentJoin(matchId, new MatchRepository.MatchStatusListener() {
            @Override
            public void onStatusChanged(String status) {
                if ("READY".equals(status) || "IN_PROGRESS".equals(status)) {
                    timeoutHandler.removeCallbacks(timeoutRunnable);
                    matchRepository.getMatch(matchId).addOnSuccessListener(match -> {
                        String oppName = "Rakip";
                        if (match != null) {
                            matchLiveData.setValue(match);
                            com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
                            String myUid = user != null ? user.getUid() : "";
                            String found = match.getOpponentUsername(myUid);
                            if (found != null && !found.isEmpty()) {
                                oppName = found;
                            }
                        }
                        com.cynorra.kpssarena.notification.NotificationHelper.showMatchOpponentJoined(context, oppName, matchId);
                        opponentJoinedName.postValue(oppName);
                        opponentReady.postValue(true);
                    }).addOnFailureListener(e -> {
                        com.cynorra.kpssarena.notification.NotificationHelper.showMatchOpponentJoined(context, "Rakip", matchId);
                        opponentReady.postValue(true);
                    });
                }
            }
            @Override
            public void onError(String error) {
                setError(context.getString(R.string.error_match_connection));
            }
        });
    }
    private void onTimeout() {
        timedOut.postValue(true);
    }
    public LiveData<Boolean> getOpponentReady() { return opponentReady; }
    public LiveData<Boolean> getTimedOut() { return timedOut; }
    public LiveData<com.cynorra.kpssarena.domain.model.Match> getMatchLiveData() { return matchLiveData; }
    public LiveData<String> getOpponentJoinedName() { return opponentJoinedName; }
    @Override
    protected void onCleared() {
        super.onCleared();
        timeoutHandler.removeCallbacks(timeoutRunnable);
        if (matchId != null) {
            matchRepository.stopListening(matchId);
        }
    }
}
