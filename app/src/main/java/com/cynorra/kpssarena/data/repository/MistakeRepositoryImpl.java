package com.cynorra.kpssarena.data.repository;

import com.cynorra.kpssarena.data.local.dao.MistakeDao;
import com.cynorra.kpssarena.data.local.entity.MistakeEntity;
import com.cynorra.kpssarena.data.remote.FirebaseFirestoreSource;
import com.cynorra.kpssarena.domain.model.Mistake;
import com.cynorra.kpssarena.domain.repository.MistakeRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        entities -> {
                            Timber.d("getAllMistakes returned %d items", entities != null ? entities.size() : 0);
                            tcs.setResult(mapEntitiesToMistakes(entities));
                        },
                        throwable -> {
                            Timber.e(throwable, "getAllMistakes failed");
                            tcs.setException(new Exception(throwable));
                        }
                );
        return tcs.getTask();
    }

    @Override
    public Task<List<Mistake>> getMistakesByCategory(String category) {
        TaskCompletionSource<List<Mistake>> tcs = new TaskCompletionSource<>();
        mistakeDao.getMistakesByCategory(category)
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        entities -> {
                            Timber.d("getMistakesByCategory(%s) returned %d items", category, entities != null ? entities.size() : 0);
                            tcs.setResult(mapEntitiesToMistakes(entities));
                        },
                        throwable -> {
                            Timber.e(throwable, "getMistakesByCategory failed");
                            tcs.setException(new Exception(throwable));
                        }
                );
        return tcs.getTask();
    }

    @Override
    public Task<Void> insertMistake(Mistake mistake) {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
        if (mistake == null) {
            tcs.setResult(null);
            return tcs.getTask();
        }
        mistakeDao.insertMistake(mapMistakeToEntity(mistake))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            Timber.d("insertMistake success for question %s", mistake.getQuestionId());
                            syncMistakeToFirestore(mistake);
                            tcs.setResult(null);
                        },
                        throwable -> {
                            Timber.e(throwable, "insertMistake failed");
                            tcs.setException(new Exception(throwable));
                        }
                );
        return tcs.getTask();
    }

    @Override
    public Task<Void> insertMistakes(List<Mistake> mistakes) {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
        if (mistakes == null || mistakes.isEmpty()) {
            tcs.setResult(null);
            return tcs.getTask();
        }
        List<MistakeEntity> entities = new ArrayList<>();
        for (Mistake m : mistakes) {
            entities.add(mapMistakeToEntity(m));
        }
        mistakeDao.insertMistakes(entities)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            Timber.d("insertMistakes success for %d items", entities.size());
                            for (Mistake m : mistakes) {
                                syncMistakeToFirestore(m);
                            }
                            tcs.setResult(null);
                        },
                        throwable -> {
                            Timber.e(throwable, "insertMistakes failed");
                            tcs.setException(new Exception(throwable));
                        }
                );
        return tcs.getTask();
    }

    @Override
    public Task<Void> markAsRetried(String questionId, boolean isCorrect) {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
        mistakeDao.markAsRetried(questionId, isCorrect)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            Timber.d("markAsRetried success for %s (correct=%s)", questionId, isCorrect);
                            updateMistakeInFirestore(questionId, isCorrect);
                            tcs.setResult(null);
                        },
                        throwable -> {
                            Timber.e(throwable, "markAsRetried failed");
                            tcs.setException(new Exception(throwable));
                        }
                );
        return tcs.getTask();
    }

    @Override
    public Task<Integer> getMistakeCount() {
        TaskCompletionSource<Integer> tcs = new TaskCompletionSource<>();
        mistakeDao.getMistakeCount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        count -> {
                            Timber.d("getMistakeCount: %d", count);
                            tcs.setResult(count);
                        },
                        throwable -> {
                            Timber.e(throwable, "getMistakeCount failed");
                            tcs.setException(new Exception(throwable));
                        }
                );
        return tcs.getTask();
    }

    private void syncMistakeToFirestore(Mistake m) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null || m == null || m.getQuestionId() == null) {
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> data = new HashMap<>();
        data.put("questionId", m.getQuestionId());
        data.put("questionText", m.getQuestionText());
        data.put("optionA", m.getOptionA());
        data.put("optionB", m.getOptionB());
        data.put("optionC", m.getOptionC());
        data.put("optionD", m.getOptionD());
        data.put("correctAnswer", m.getCorrectAnswer());
        data.put("userAnswer", m.getUserAnswer());
        data.put("category", m.getCategory());
        data.put("explanation", m.getExplanation());
        data.put("testDate", m.getTestDate());
        data.put("isRetried", m.isRetried());
        data.put("isRetryCorrect", m.isRetryCorrect());
        data.put("createdAt", m.getCreatedAt());

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("mistakes")
                .document(m.getQuestionId())
                .set(data, SetOptions.merge())
                .addOnFailureListener(e -> Timber.w(e, "Could not sync mistake %s to Firestore", m.getQuestionId()));
    }

    private void updateMistakeInFirestore(String questionId, boolean isCorrect) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null || questionId == null) {
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> updates = new HashMap<>();
        updates.put("isRetried", true);
        updates.put("isRetryCorrect", isCorrect);

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("mistakes")
                .document(questionId)
                .set(updates, SetOptions.merge())
                .addOnFailureListener(e -> Timber.w(e, "Could not update retried state in Firestore"));
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
