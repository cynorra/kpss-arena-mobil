package com.cynorra.kpssarena.presentation.admin;
import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.data.remote.FirebaseFirestoreSource;
import com.cynorra.kpssarena.data.remote.model.DailyTestDto;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
@HiltViewModel
public class AdminViewModel extends BaseViewModel {
    private final FirebaseFirestoreSource firestoreSource;
    private final Context context;
    private final MutableLiveData<List<ScheduledTestItem>> scheduledTests = new MutableLiveData<>();
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
        firestoreSource.getScheduledTests(30)
                .addOnSuccessListener(snapshot -> {
                    setLoading(false);
                    List<ScheduledTestItem> items = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        DailyTestDto dto = doc.toObject(DailyTestDto.class);
                        if (dto != null) {
                            int questionCount = dto.getQuestions() != null ? dto.getQuestions().size() : 0;
                            items.add(new ScheduledTestItem(doc.getId(), questionCount, dto.isActive()));
                        }
                    }
                    scheduledTests.setValue(items);
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError(context.getString(R.string.error_scheduled_tests_load_failed));
                });
    }
    public LiveData<List<ScheduledTestItem>> getScheduledTests() { return scheduledTests; }
}
