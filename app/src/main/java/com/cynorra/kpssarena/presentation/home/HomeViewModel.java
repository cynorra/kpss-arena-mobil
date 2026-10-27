package com.cynorra.kpssarena.presentation.home;
import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.domain.model.DailyTest;
import com.cynorra.kpssarena.domain.model.LeaderboardEntry;
import com.cynorra.kpssarena.domain.model.User;
import com.cynorra.kpssarena.domain.repository.AuthRepository;
import com.cynorra.kpssarena.domain.repository.LeaderboardRepository;
import com.cynorra.kpssarena.domain.repository.TestRepository;
import com.cynorra.kpssarena.domain.usecase.leaderboard.GetLeaderboardUseCase;
import com.cynorra.kpssarena.domain.usecase.test.GetDailyTestUseCase;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import com.google.firebase.auth.FirebaseAuth;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
@HiltViewModel
public class HomeViewModel extends BaseViewModel {
    private final GetDailyTestUseCase getDailyTestUseCase;
    private final GetLeaderboardUseCase getLeaderboardUseCase;
    private final AuthRepository authRepository;
    private final TestRepository testRepository;
    private final Context context;

    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<DailyTest> dailyTestLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> testCompleted = new MutableLiveData<>();
    private final MutableLiveData<List<LeaderboardEntry>> miniLeaderboard = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navigateToTest = new MutableLiveData<>();
    private final MutableLiveData<com.cynorra.kpssarena.domain.model.ScoreResult> navigateToResult = new MutableLiveData<>();
    @Inject
    public HomeViewModel(GetDailyTestUseCase getDailyTestUseCase,
                         GetLeaderboardUseCase getLeaderboardUseCase,
                         AuthRepository authRepository,
                         TestRepository testRepository,
                         @ApplicationContext Context context) {
        this.getDailyTestUseCase = getDailyTestUseCase;
        this.getLeaderboardUseCase = getLeaderboardUseCase;
        this.authRepository = authRepository;
        this.testRepository = testRepository;
        this.context = context;
        loadUserData();
        loadDailyTestStatus();
        loadMiniLeaderboard();
    }
    private void loadUserData() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            setError(context.getString(R.string.error_no_session));
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        authRepository.getUserProfile(uid)
                .addOnSuccessListener(user -> {
                    userLiveData.postValue(user);
                })
                .addOnFailureListener(e -> {
                    setError(context.getString(R.string.error_profile_load_failed));
                });
    }
    private void loadDailyTestStatus() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getDailyTestUseCase.hasCompletedToday(uid)
                .addOnSuccessListener(completed -> {
                    testCompleted.postValue(completed);
                })
                .addOnFailureListener(e -> {
                    setError(context.getString(R.string.error_daily_test_status_failed));
                });
    }

    private void loadMiniLeaderboard() {
        getLeaderboardUseCase.getTodayTop(5)
                .addOnSuccessListener(entries -> {
                    miniLeaderboard.postValue(entries);
                })
                .addOnFailureListener(e -> {
                    setError(context.getString(R.string.error_leaderboard_mini_load_failed));
                });
    }
    public void startDailyTest() {
        Boolean completed = testCompleted.getValue();
        if (completed != null && completed) {
            showTodayResult();
        } else {
            setLoading(true);
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            getDailyTestUseCase.execute(today)
                    .addOnSuccessListener(test -> {
                        setLoading(false);
                        if (test != null && test.getQuestions() != null && !test.getQuestions().isEmpty()) {
                            navigateToTest.postValue(true);
                        } else {
                            setError(context.getString(R.string.error_daily_test_not_published));
                        }
                    })
                    .addOnFailureListener(e -> {
                        setLoading(false);
                        setError(context.getString(R.string.error_test_load_generic));
                    });
        }
    }
    public void refreshData() {
        loadUserData();
        loadDailyTestStatus();
        loadMiniLeaderboard();
    }
    /** The button reads "View Result" once today's test is done: open the saved result instead of failing. */
    private void showTodayResult() {
        com.google.firebase.auth.FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            setError(context.getString(R.string.error_no_session));
            return;
        }
        setLoading(true);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        testRepository.getTodayScoreResult(today, user.getUid())
                .addOnSuccessListener(result -> {
                    setLoading(false);
                    navigateToResult.postValue(result);
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError(context.getString(R.string.error_test_load_generic));
                });
    }
    public void onResultNavigated() {
        navigateToResult.postValue(null);
    }
    public LiveData<com.cynorra.kpssarena.domain.model.ScoreResult> getNavigateToResult() { return navigateToResult; }
    public void onNavigated() {

        navigateToTest.postValue(false);
    }
    public LiveData<User> getUserLiveData() { return userLiveData; }
    public LiveData<DailyTest> getDailyTestLiveData() { return dailyTestLiveData; }
    public LiveData<Boolean> getTestCompleted() { return testCompleted; }
    public LiveData<List<LeaderboardEntry>> getMiniLeaderboard() { return miniLeaderboard; }
    public LiveData<Boolean> getNavigateToTest() { return navigateToTest; }
}
