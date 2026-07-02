package com.cynorra.kpssarena.domain.repository;
import com.cynorra.kpssarena.domain.model.DailyTest;
import com.cynorra.kpssarena.domain.model.Question;
import com.cynorra.kpssarena.domain.model.ScoreResult;

import com.google.android.gms.tasks.Task;
import java.util.List;
public interface TestRepository {
    Task<DailyTest> getDailyTest(String date);
    Task<ScoreResult> submitTest(String date, String uid, DailyTest test, List<Question> q
    Task<Boolean> hasCompletedTodayTest(String date, String uid);
    Task<List<ScoreResult>> getUserTestHistory(String uid, int limit);
    ScoreResult calculateScore(List<Question> questions, int totalTimeUsed, int totalTime
}
