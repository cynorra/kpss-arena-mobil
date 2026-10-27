package com.cynorra.kpssarena.presentation.arena;
import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.domain.model.Match;
import com.cynorra.kpssarena.domain.repository.MatchRepository;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import com.google.firebase.auth.FirebaseAuth;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
@HiltViewModel
public class MatchResultViewModel extends BaseViewModel {
    private final MatchRepository matchRepository;
    private final Context context;
    private final MutableLiveData<Match> match = new MutableLiveData<>();
    private final String myUid;
    @Inject
    public MatchResultViewModel(MatchRepository matchRepository, @ApplicationContext Context context) {
        this.matchRepository = matchRepository;
        this.context = context;
        com.google.firebase.auth.FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.myUid = currentUser != null ? currentUser.getUid() : "";
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
                    setError(context.getString(R.string.error_match_result_load_failed));
                });
    }
    public String getMyUid() { return myUid; }
    public LiveData<Match> getMatch() { return match; }
}
