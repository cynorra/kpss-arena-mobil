package com.cynorra.kpssarena.data.repository;
import com.cynorra.kpssarena.data.local.dao.MistakeDao;
import com.cynorra.kpssarena.data.local.entity.MistakeEntity;
import com.cynorra.kpssarena.data.remote.FirebaseFirestoreSource;
import com.cynorra.kpssarena.domain.model.Mistake;
import com.cynorra.kpssarena.domain.repository.MistakeRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
public class MistakeRepositoryImpl implements MistakeRepository {
    private final MistakeDao mistakeDao;
    private final FirebaseFirestoreSource firestoreSource;
    @Inject
    public MistakeRepositoryImpl(MistakeDao mistakeDao, FirebaseFirestoreSource firestoreSource) {
        this.mistakeDao = mistakeDao;
        this.firestoreSource = firestoreSource;
    }
    @Override
    public Task<List<Mistake>> getAllMistakes() {
        TaskCompletionSource<List<Mistake>> tcs = new TaskCompletionSource<>();
        mistakeDao.getAllMistakes()
                .firstOrError()
                .subscribe(
                        entities -> tcs.setResult(mapEntitiesToMistakes(entities)),
                        throwable -> tcs.setException(new Exception(throwable))
                );
        return tcs.getTask();
    }
    @Override
    public Task<List<Mistake>> getMistakesByCategory(String category) {
        TaskCompletionSource<List<Mistake>> tcs = new TaskCompletionSource<>();
        mistakeDao.getMistakesByCategory(category)
                .firstOrError()
                .subscribe(
                        entities -> tcs.setResult(mapEntitiesToMistakes(entities)),
                        throwable -> tcs.setException(new Exception(throwable))
                );
        return tcs.getTask();
    }
    @Override
    public Task<Void> insertMistake(Mistake mistake) {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
        mistakeDao.insertMistake(mapMistakeToEntity(mistake))
                .subscribe(
                        () -> tcs.setResult(null),
                        throwable -> tcs.setException(new Exception(throwable))
                );
        return tcs.getTask();
    }
    @Override
    public Task<Void> markAsRetried(String questionId, boolean isCorrect) {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
        mistakeDao.markAsRetried(questionId, isCorrect)
                .subscribe(
                        () -> tcs.setResult(null),
                        throwable -> tcs.setException(new Exception(throwable))
                );
        return tcs.getTask();
    }
    @Override
    public Task<Integer> getMistakeCount() {
        TaskCompletionSource<Integer> tcs = new TaskCompletionSource<>();
        mistakeDao.getMistakeCount()
                .subscribe(
                        tcs::setResult,
                        throwable -> tcs.setException(new Exception(throwable))
                );
        return tcs.getTask();
    }
    private List<Mistake> mapEntitiesToMistakes(List<MistakeEntity> entities) {
        List<Mistake> mistakes = new ArrayList<>();
        for (MistakeEntity entity : entities) {
            mistakes.add(mapEntityToMistake(entity));
        }
        return mistakes;
    }
    private Mistake mapEntityToMistake(MistakeEntity entity) {
        Mistake mistake = new Mistake();
        mistake.setQuestionId(entity.getQuestionId());
        mistake.setQuestionText(entity.getQuestionText());
        mistake.setOptionA(entity.getOptionA());
        mistake.setOptionB(entity.getOptionB());
        mistake.setOptionC(entity.getOptionC());
        mistake.setOptionD(entity.getOptionD());
        mistake.setCorrectAnswer(entity.getCorrectAnswer());
        mistake.setUserAnswer(entity.getUserAnswer());
        mistake.setCategory(entity.getCategory());
        mistake.setExplanation(entity.getExplanation());
        mistake.setTestDate(entity.getTestDate());
        mistake.setRetried(entity.isRetried());
        mistake.setRetryCorrect(entity.isRetryCorrect());
        mistake.setCreatedAt(entity.getCreatedAt());
        return mistake;
    }
    private MistakeEntity mapMistakeToEntity(Mistake mistake) {
        MistakeEntity entity = new MistakeEntity(
                mistake.getQuestionId(), mistake.getQuestionText(),
                mistake.getOptionA(), mistake.getOptionB(),
                mistake.getOptionC(), mistake.getOptionD(),
                mistake.getCorrectAnswer(), mistake.getUserAnswer(),
                mistake.getCategory(), mistake.getExplanation(),
                mistake.getTestDate());
        return entity;
    }
}
