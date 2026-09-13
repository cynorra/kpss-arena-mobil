package com.cynorra.kpssarena.presentation.arena;
import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.domain.model.Match;
import com.cynorra.kpssarena.domain.usecase.match.CreateMatchUseCase;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
@HiltViewModel
public class ArenaViewModel extends BaseViewModel {
    private final CreateMatchUseCase createMatchUseCase;
    private final Context context;
    private final MutableLiveData<List<Match>> activeMatches = new MutableLiveData<>();
    private final MutableLiveData<String> navigateToWaiting = new MutableLiveData<>();
    @Inject
    public ArenaViewModel(CreateMatchUseCase createMatchUseCase, @ApplicationContext Context context) {
        this.createMatchUseCase = createMatchUseCase;
        this.context = context;
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
}
