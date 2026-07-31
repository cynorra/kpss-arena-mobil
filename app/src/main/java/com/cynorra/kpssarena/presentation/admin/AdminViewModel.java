package com.cynorra.kpssarena.presentation.admin;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.data.remote.FirebaseFirestoreSource;
import com.cynorra.kpssarena.data.remote.model.DailyTestDto;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
@HiltViewModel
public class AdminViewModel extends BaseViewModel {
    private final FirebaseFirestoreSource firestoreSource;
    private final MutableLiveData<List<DailyTestDto>> scheduledTests = new MutableLiveData<>();
    @Inject
    public AdminViewModel(FirebaseFirestoreSource firestoreSource) {
        this.firestoreSource = firestoreSource;
    }
    public void addQuestion(DailyTestDto.QuestionDto question) {
        setLoading(true);
        firestoreSource.addQuestionToPool(question)
                .addOnSuccessListener(v -> {
                    setLoading(false);
                    setSuccess("Soru havuza eklendi.");
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError("Soru eklenirken hata oluştu.");
                });
    }
    public void loadScheduledTests() {
        setLoading(true);
        // Load upcoming scheduled tests
        setLoading(false);
    }
    public LiveData<List<DailyTestDto>> getScheduledTests() { return scheduledTests; }
}
