package com.cynorra.kpssarena.presentation.home;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
@HiltViewModel
public class HomeViewModel extends BaseViewModel {
    private final GetDailyTestUseCase getDailyTestUseCase;
    private final GetLeaderboardUseCase getLeaderboardUseCase;
    private final AuthRepository authRepository;

    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<DailyTest> dailyTestLiveData = new MutableLiveData<>
    private final MutableLiveData<Boolean> testCompleted = new MutableLiveData<>();
    private final MutableLiveData<List<LeaderboardEntry>> miniLeaderboard = new Muta
    private final MutableLiveData<Boolean> navigateToTest = new MutableLiveData<>();
    @Inject
    public HomeViewModel(GetDailyTestUseCase getDailyTestUseCase,
                         GetLeaderboardUseCase getLeaderboardUseCase,
                         AuthRepository authRepository) {
        this.getDailyTestUseCase = getDailyTestUseCase;
        this.getLeaderboardUseCase = getLeaderboardUseCase;
        this.authRepository = authRepository;
        loadUserData();
        loadDailyTestStatus();
        loadMiniLeaderboard();
    }
    private void loadUserData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        authRepository.getUserProfile(uid)
                .addOnSuccessListener(user -> {
                    userLiveData.postValue(user);
                })
                .addOnFailureListener(e -> {
                    setError("Profil bilgileri yüklenemedi.");
                });
    }
    private void loadDailyTestStatus() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(ne
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getDailyTestUseCase.hasCompletedToday(uid)
                .addOnSuccessListener(completed -> {
                    testCompleted.postValue(completed);
                });
    }

    private void loadMiniLeaderboard() {
        getLeaderboardUseCase.getTodayTop(5)
                .addOnSuccessListener(entries -> {
                    miniLeaderboard.postValue(entries);
                })
                .addOnFailureListener(e -> {
                    setError("Liderlik tablosu yüklenemedi.");
                });
    }
    public void startDailyTest() {
        Boolean completed = testCompleted.getValue();
        if (completed != null && completed) {
            setError("Bugünkü testi zaten çözdün. Yarın tekrar gel!");
        } else {
            setLoading(true);
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
            getDailyTestUseCase.execute(today)
                    .addOnSuccessListener(test -> {
                        setLoading(false);
                        if (test != null && test.getQuestions() != null && !test.getQuestions().isEmpty
                            navigateToTest.postValue(true);
                        } else {
                            setError("Bugün için test henüz yayınlanmadı. Saat 09:00'da aktif olacak.
                        }
                    })
                    .addOnFailureListener(e -> {
                        setLoading(false);
                        setError("Test yüklenirken hata oluştu.");
                    });
        }
    }
    public void refreshData() {
        loadUserData();
        loadDailyTestStatus();
        loadMiniLeaderboard();
    }
    public void onNavigated() {

        navigateToTest.postValue(false);
    }
    public LiveData<User> getUserLiveData() { return userLiveData; }
    public LiveData<DailyTest> getDailyTestLiveData() { return dailyTestLiveData; }
    public LiveData<Boolean> getTestCompleted() { return testCompleted; }
    public LiveData<List<LeaderboardEntry>> getMiniLeaderboard() { return miniLeaderb
    public LiveData<Boolean> getNavigateToTest() { return navigateToTest; }
}
