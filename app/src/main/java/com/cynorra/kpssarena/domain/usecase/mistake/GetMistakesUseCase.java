package com.cynorra.kpssarena.domain.usecase.mistake;
import com.cynorra.kpssarena.domain.model.Mistake;
import com.cynorra.kpssarena.domain.repository.MistakeRepository;
import com.google.android.gms.tasks.Task;
import java.util.List;
import javax.inject.Inject;
public class GetMistakesUseCase {
    private final MistakeRepository mistakeRepository;
    @Inject
    public GetMistakesUseCase(MistakeRepository mistakeRepository) {
        this.mistakeRepository = mistakeRepository;
    }
    public Task<List<Mistake>> getAll() {
        return mistakeRepository.getAllMistakes();
    }
    public Task<List<Mistake>> getByCategory(String category) {
        return mistakeRepository.getMistakesByCategory(category);
    }
}
