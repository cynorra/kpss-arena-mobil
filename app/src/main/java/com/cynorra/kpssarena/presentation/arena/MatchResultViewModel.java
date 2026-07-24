package com.cynorra.kpssarena.presentation.arena;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.domain.model.Match;
import com.cynorra.kpssarena.domain.repository.MatchRepository;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import com.google.firebase.auth.FirebaseAuth;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
@HiltViewModel
public class MatchResultViewModel extends BaseViewModel {
    private final MatchRepository matchRepository;
    private final MutableLiveData<Match> match = new MutableLiveData<>();
    private final String myUid;
    @Inject
    public MatchResultViewModel(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
        this.myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    public void loadMatch(String matchId) {
        setLoading(true);
        matchRepository.getMatch(matchId)
                .addOnSuccessListener(m -> {
                    setLoading(false);
                    match.setValue(m);
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError("Sonuç yüklenemedi.");
                });
    }
    public String getMyUid() { return myUid; }
    public LiveData<Match> getMatch() { return match; }
}
