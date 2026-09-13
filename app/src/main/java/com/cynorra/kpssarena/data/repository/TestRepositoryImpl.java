package com.cynorra.kpssarena.data.repository;
import android.content.Context;
import com.cynorra.kpssarena.R;
import com.cynorra.kpssarena.data.local.dao.UserCacheDao;
import com.cynorra.kpssarena.data.remote.FirebaseFirestoreSource;
import com.cynorra.kpssarena.data.remote.model.DailyTestDto;
import com.cynorra.kpssarena.data.remote.model.ScoreDto;
import com.cynorra.kpssarena.domain.model.DailyTest;
import com.cynorra.kpssarena.domain.model.Question;
import com.cynorra.kpssarena.domain.model.ScoreResult;
import com.cynorra.kpssarena.domain.repository.TestRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import dagger.hilt.android.qualifiers.ApplicationContext;
public class TestRepositoryImpl implements TestRepository {
    private final FirebaseFirestoreSource firestoreSource;
    private final UserCacheDao userCacheDao;
    private final FirebaseAnalytics analytics;
    private final Context context;
    @Inject
    public TestRepositoryImpl(FirebaseFirestoreSource firestoreSource,
                              UserCacheDao userCacheDao,
                              FirebaseAnalytics analytics,
                              @ApplicationContext Context context) {
        this.firestoreSource = firestoreSource;
        this.userCacheDao = userCacheDao;
        this.analytics = analytics;
        this.context = context;
    }
    @Override

    public Task<DailyTest> getDailyTest(String date) {
        return firestoreSource.getDailyTest(date)
                .continueWithTask(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (task.getResult().exists()) {
                            DailyTestDto dto = task.getResult().toObject(DailyTestDto.class);
                            return Tasks.forResult(mapDtoToDailyTest(date, dto));
                        } else {
                            return generateAndSaveLazyTest(date);
                        }
                    } else {
                        return Tasks.forException(task.getException() != null ? task.getException() : new Exception(context.getString(R.string.error_test_load_generic_exc)));
                    }
                });
    }

    private Task<DailyTest> generateAndSaveLazyTest(String date) {
        return firestoreSource.getRandomQuestionsFromPool(15)
                .continueWithTask(poolTask -> {
                    if (poolTask.isSuccessful() && poolTask.getResult() != null) {
                        List<DailyTestDto.QuestionDto> questionDtos = new ArrayList<>();
                        for (com.google.firebase.firestore.DocumentSnapshot doc : poolTask.getResult().getDocuments()) {
                            DailyTestDto.QuestionDto qDto = doc.toObject(DailyTestDto.QuestionDto.class);
                            if (qDto != null) {
                                if (qDto.getId() == null) qDto.setId(doc.getId());
                                questionDtos.add(qDto);
                            }
                        }
                        
                        if (questionDtos.isEmpty()) {
                            return Tasks.forException(new Exception(context.getString(R.string.error_question_pool_empty)));
                        }
                        
                        DailyTestDto testDto = new DailyTestDto();
                        testDto.setQuestions(questionDtos);
                        testDto.setActive(true);
                        testDto.setPublishedAt(System.currentTimeMillis());
                        
                        return firestoreSource.createDailyTest(date, testDto)
                                .continueWith(saveTask -> {
                                    if (saveTask.isSuccessful()) {
                                        return mapDtoToDailyTest(date, testDto);
                                    }
                                    throw saveTask.getException() != null ? saveTask.getException() : new Exception(context.getString(R.string.error_test_save_failed));
                                });
                    }
                    throw poolTask.getException() != null ? poolTask.getException() : new Exception(context.getString(R.string.error_questions_fetch_failed));
                });
    }
    @Override
    public Task<ScoreResult> submitTest(String date, String uid, DailyTest test, List<Question> questions) {
        ScoreResult result = calculateScore(questions,
                test.getTotalTime() * 1000,
                test.getTotalTime() * 1000);
        result.setCompletedAt(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        ScoreDto scoreDto = new ScoreDto();
        scoreDto.setUid(uid);
        scoreDto.setNetScore(result.getNetScore());
        scoreDto.setSpeedBonus(result.getSpeedBonus());
        scoreDto.setTotalScore(result.getTotalScore());
        scoreDto.setCompletedAt(result.getCompletedAt());
        scoreDto.setDurationSeconds(result.getTotalTimeUsed() / 1000);
        Map<String, String> answers = new HashMap<>();
        for (int i = 0; i < questions.size(); i++) {
            String answer = questions.get(i).getUserAnswer();
            if (answer != null) {
                answers.put("q" + i, answer);
            }
        }
        scoreDto.setAnswers(answers);
        analytics.logEvent("test_completed", null);
        return firestoreSource.submitTestScore(date, uid, scoreDto)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        result.setAnswers(answers);
                        return result;

                    }
                    throw task.getException() != null ? task.getException() : new Exception(context.getString(R.string.error_score_submit_failed));
                });
    }
    @Override
    public Task<Boolean> hasCompletedTodayTest(String date, String uid) {
        return firestoreSource.getUserScore(date, uid)
                .continueWith(task -> task.isSuccessful() && task.getResult() != null && task.getResult().exists());
    }
    @Override
    public Task<List<ScoreResult>> getUserTestHistory(String uid, int limit) {
        return firestoreSource.getUserTestHistory(uid, limit)
                .continueWith(task -> {
                    List<ScoreResult> history = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (com.google.firebase.firestore.DocumentSnapshot doc : task.getResult().getDocuments()) {
                            ScoreDto dto = doc.toObject(ScoreDto.class);
                            if (dto != null) {
                                history.add(mapDtoToScoreResult(dto));
                            }
                        }
                    }
                    return history;
                });
    }
    @Override
    public Task<Integer> getUserRank(String date, int totalScore) {
        return firestoreSource.getRankForScore(date, totalScore)
                .continueWith(task -> task.isSuccessful() && task.getResult() != null
                        ? task.getResult().intValue() : 1);
    }
    private ScoreResult mapDtoToScoreResult(ScoreDto dto) {
        ScoreResult result = new ScoreResult();
        result.setNetScore(dto.getNetScore());
        result.setSpeedBonus(dto.getSpeedBonus());
        result.setTotalScore(dto.getTotalScore());
        result.setCompletedAt(dto.getCompletedAt());
        result.setTotalTimeUsed((int) (dto.getDurationSeconds() * 1000));
        result.setAnswers(dto.getAnswers());
        return result;
    }
    @Override
    public ScoreResult calculateScore(List<Question> questions, int totalTimeUsed, int totalTimeAvailable) {
        int correctCount = 0;
        int wrongCount = 0;
        int emptyCount = 0;
        for (Question q : questions) {
            if (q.getUserAnswer() == null || q.getUserAnswer().isEmpty()) {
                emptyCount++;
            } else if (q.getUserAnswer().equals(q.getCorrectAnswer())) {
                correctCount++;
            } else {
                wrongCount++;
            }
        }
        int netScore = correctCount * 4 - wrongCount * 1;
        int timeRatio = totalTimeAvailable > 0 ? 
                (totalTimeAvailable - totalTimeUsed) * 20 / totalTimeAvailable : 0;
        int speedBonus = Math.max(0, Math.min(20, timeRatio));
        int totalScore = netScore + speedBonus;
        ScoreResult result = new ScoreResult();
        result.setCorrectCount(correctCount);

        result.setWrongCount(wrongCount);
        result.setEmptyCount(emptyCount);
        result.setNetScore(netScore);
        result.setSpeedBonus(speedBonus);
        result.setTotalScore(totalScore);
        result.setTotalTimeUsed(totalTimeUsed);
        return result;
    }
    private DailyTest mapDtoToDailyTest(String date, DailyTestDto dto) {
        if (dto == null) return null;
        DailyTest test = new DailyTest();
        test.setDate(date);
        test.setPublishedAt(dto.getPublishedAt());
        test.setActive(dto.isActive());
        List<Question> questions = new ArrayList<>();
        if (dto.getQuestions() != null) {
            for (DailyTestDto.QuestionDto qDto : dto.getQuestions()) {
                Question q = new Question(
                        qDto.getId(),
                        qDto.getText(),
                        qDto.getOptionA(),
                        qDto.getOptionB(),
                        qDto.getOptionC(),
                        qDto.getOptionD(),
                        qDto.getCorrect(),
                        qDto.getCategory(),
                        qDto.getExplanation()
                );
                questions.add(q);
            }
        }
        test.setQuestions(questions);
        test.setTotalQuestions(questions.size());
        return test;
    }
}
