package com.cynorra.kpssarena.domain.repository;
import com.cynorra.kpssarena.domain.model.Mistake;
import com.google.android.gms.tasks.Task;
import java.util.List;
public interface MistakeRepository {
    Task<List<Mistake>> getAllMistakes();
    Task<List<Mistake>> getMistakesByCategory(String category);
    Task<Void> insertMistake(Mistake mistake);
    Task<Void> markAsRetried(String questionId, boolean isCorrect);
    Task<Integer> getMistakeCount();
}
