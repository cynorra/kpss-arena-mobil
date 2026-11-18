package com.cynorra.kpssarena.domain.repository;
import com.cynorra.kpssarena.domain.model.DailyTest;
import com.cynorra.kpssarena.domain.model.Question;
import com.cynorra.kpssarena.domain.model.ScoreResult;

import com.google.android.gms.tasks.Task;
import java.util.List;
public interface TestRepository {
    Task<DailyTest> getDailyTest(String date);
    Task<ScoreResult> submitTest(String date, String uid, DailyTest test, List<Question> questions);
    Task<Boolean> hasCompletedTodayTest(String date, String uid);
    Task<ScoreResult> getTodayScoreResult(String date, String uid);
    Task<List<ScoreResult>> getUserTestHistory(String uid, int limit);
    Task<Integer> getUserRank(String date, int totalScore);
    ScoreResult calculateScore(List<Question> questions, int totalTimeUsed, int totalTimeAvailable);
    /** category null/"Tümü" ise tum havuzdan, degilse sadece o kategoriden rastgele soru getirir. */
    Task<List<Question>> getPracticeQuestions(String category, int limit);
    /** Gercek KPSS formatında (120 soru, gercek OSYM dagilimi) tam deneme sinavi sorulari. */
    Task<List<Question>> getMockExamQuestions();
    /** Belirtilen sinav turune gore (Tam KPSS, GY, GK, Brans) sirali sorular getirir. */
    Task<List<Question>> getExamQuestions(com.cynorra.kpssarena.domain.model.ExamType examType);
    /** Belirtilen sinav turu ve deneme numarasina (Deneme 1, 2, 3...) gore deterministik sorular getirir. */
    Task<List<Question>> getExamQuestions(com.cynorra.kpssarena.domain.model.ExamType examType, int testNumber);
    /** Deneme sinavi sonucunu hesaplar ve kullanicinin kendi mockExamResults gecmisine kaydeder (gunluk siralama/seriye dahil edilmez). */
    Task<ScoreResult> submitMockExam(String uid, List<Question> questions, int totalTimeUsed, int totalTimeAvailable);
    Task<ScoreResult> submitMockExam(String uid, com.cynorra.kpssarena.domain.model.ExamType examType, List<Question> questions, int totalTimeUsed, int totalTimeAvailable);
    Task<ScoreResult> submitMockExam(String uid, com.cynorra.kpssarena.domain.model.ExamType examType, int testNumber, List<Question> questions, int totalTimeUsed, int totalTimeAvailable);
}
