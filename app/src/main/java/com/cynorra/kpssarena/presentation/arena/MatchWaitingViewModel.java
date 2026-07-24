package com.cynorra.kpssarena.presentation.arena;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.domain.repository.MatchRepository;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
@HiltViewModel
public class MatchWaitingViewModel extends BaseViewModel {
    private final MatchRepository matchRepository;
    private final MutableLiveData<Boolean> opponentReady = new MutableLiveData<>(false);
    private String matchId;
    @Inject
    public MatchWaitingViewModel(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }
    public void startWatching(String matchId) {
        this.matchId = matchId;
        matchRepository.listenForOpponentJoin(matchId, new MatchRepository.MatchStatusListener() {
            @Override
            public void onStatusChanged(String status) {
                if ("READY".equals(status) || "IN_PROGRESS".equals(status)) {
                    opponentReady.postValue(true);
                }
            }
            @Override
            public void onError(String error) {
                setError("Bağlantı hatası oluştu.");
            }
        });
    }
    public LiveData<Boolean> getOpponentReady() { return opponentReady; }
    @Override
    protected void onCleared() {
        super.onCleared();
        if (matchId != null) {
            matchRepository.stopListening(matchId);
        }
    }
}
