package com.cynorra.kpssarena.presentation.mistakes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.cynorra.kpssarena.domain.model.Mistake;

import com.cynorra.kpssarena.domain.usecase.mistake.GetMistakesUseCase;
import com.cynorra.kpssarena.presentation.base.BaseViewModel;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
@HiltViewModel
public class MistakesViewModel extends BaseViewModel {
    private final GetMistakesUseCase getMistakesUseCase;
    private final MutableLiveData<List<Mistake>> mistakes = new MutableLiveData<>();
    @Inject
    public MistakesViewModel(GetMistakesUseCase getMistakesUseCase) {
        this.getMistakesUseCase = getMistakesUseCase;
        loadMistakes();
    }
    private void loadMistakes() {
        setLoading(true);
        getMistakesUseCase.getAll()
                .addOnSuccessListener(list -> {
                    setLoading(false);
                    mistakes.setValue(list);
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    setError("Yanlış arşivi yüklenemedi.");
                });
    }
    public void filterByCategory(String category) {
        if (category == null) {
            loadMistakes();
        } else {
            setLoading(true);
            getMistakesUseCase.getByCategory(category)

                    .addOnSuccessListener(list -> {
                        setLoading(false);
                        mistakes.setValue(list);
                    });
        }
    }
    public void retryAll() {
        // Navigate to retry mode
    }
    public LiveData<List<Mistake>> getMistakes() { return mistakes; }
}
