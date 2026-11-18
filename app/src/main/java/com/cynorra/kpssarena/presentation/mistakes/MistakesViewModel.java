package com.cynorra.kpssarena.presentation.mistakes;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.domain.model.Mistake;
import com.cynorra.kpssarena.domain.repository.MistakeRepository;
import com.cynorra.kpssarena.domain.usecase.mistake.GetMistakesUseCase;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import timber.log.Timber;

@HiltViewModel
public class MistakesViewModel extends BaseViewModel {
    private final GetMistakesUseCase getMistakesUseCase;
    private final MistakeRepository mistakeRepository;
    private final Context context;

    private final MutableLiveData<List<Mistake>> mistakes = new MutableLiveData<>();
    private final MutableLiveData<List<Mistake>> retryMistakesEvent = new MutableLiveData<>();
    private String currentCategory = null;

    @Inject
    public MistakesViewModel(GetMistakesUseCase getMistakesUseCase,
                             MistakeRepository mistakeRepository,
                             @ApplicationContext Context context) {
        this.getMistakesUseCase = getMistakesUseCase;
        this.mistakeRepository = mistakeRepository;
        this.context = context;
        loadMistakes();
    }

    public void refresh() {
        filterByCategory(currentCategory);
    }

    public void loadMistakes() {
        currentCategory = null;
        setLoading(true);
        getMistakesUseCase.getAll()
                .addOnSuccessListener(list -> {
                    setLoading(false);
                    mistakes.setValue(list != null ? list : new ArrayList<>());
                })
                .addOnFailureListener(e -> {
                    Timber.e(e, "Error loading mistakes");
                    setLoading(false);
                    setError(context.getString(R.string.error_mistakes_load_failed));
                });
    }

    public void filterByCategory(String category) {
        currentCategory = category;
        if (category == null || category.isEmpty() || "Tümü".equalsIgnoreCase(category) || "All".equalsIgnoreCase(category)) {
            loadMistakes();
        } else {
            setLoading(true);
            getMistakesUseCase.getByCategory(category)
                    .addOnSuccessListener(list -> {
                        setLoading(false);
                        mistakes.setValue(list != null ? list : new ArrayList<>());
                    })
                    .addOnFailureListener(e -> {
                        Timber.e(e, "Error filtering mistakes by category %s", category);
                        setLoading(false);
                        setError(context.getString(R.string.error_mistakes_load_failed));
                    });
        }
    }

    public void markRetried(String questionId, boolean isCorrect) {
        mistakeRepository.markAsRetried(questionId, isCorrect)
                .addOnSuccessListener(unused -> refresh());
    }

    public void retryAll() {
        List<Mistake> current = mistakes.getValue();
        if (current != null && !current.isEmpty()) {
            retryMistakesEvent.setValue(current);
        }
    }

    public void onRetryHandled() {
        retryMistakesEvent.setValue(null);
    }

    public LiveData<List<Mistake>> getMistakes() { return mistakes; }
    public LiveData<List<Mistake>> getRetryMistakesEvent() { return retryMistakesEvent; }
    public String getCurrentCategory() { return currentCategory; }
}
