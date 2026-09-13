package com.cynorra.kpssarena.presentation.admin;
import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.data.remote.FirebaseFirestoreSource;
import com.cynorra.kpssarena.data.remote.model.DailyTestDto;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
@HiltViewModel
public class AdminViewModel extends BaseViewModel {
    private final FirebaseFirestoreSource firestoreSource;
    private final Context context;
    private final MutableLiveData<List<DailyTestDto>> scheduledTests = new MutableLiveData<>();
    @Inject
    public AdminViewModel(FirebaseFirestoreSource firestoreSource, @ApplicationContext Context context) {
        this.firestoreSource = firestoreSource;
        this.context = context;
    }
    public void addQuestion(DailyTestDto.QuestionDto question) {
        setLoading(true);
        firestoreSource.addQuestionToPool(question)
                .addOnSuccessListener(v -> {
                    setLoading(false);
                    setSuccess(context.getString(R.string.success_question_added));
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError(context.getString(R.string.error_question_add_failed));
                });
    }
    public void loadScheduledTests() {
        setLoading(true);
        // Load upcoming scheduled tests
        setLoading(false);
    }
    public LiveData<List<DailyTestDto>> getScheduledTests() { return scheduledTests; }
}
