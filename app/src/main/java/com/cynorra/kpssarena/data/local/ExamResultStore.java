package com.cynorra.kpssarena.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.cynorra.kpssarena.domain.model.ExamType;
import com.cynorra.kpssarena.domain.model.ScoreResult;

import org.json.JSONObject;

import java.util.EnumMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class ExamResultStore {

    private static final String PREF_NAME = "mock_exam_results_store";
    private final SharedPreferences prefs;

    @Inject
    public ExamResultStore(@ApplicationContext Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveResult(String uid, ExamType examType, int testNumber, ScoreResult result) {
        if (uid == null || examType == null || result == null) return;
        try {
            JSONObject json = new JSONObject();
            json.put("examType", examType.name());
            json.put("testNumber", testNumber);
            json.put("examTitle", result.getExamTitle() != null ? result.getExamTitle() : examType.getTitle());
            json.put("netScore", result.getNetScore());
            json.put("correctCount", result.getCorrectCount());
            json.put("wrongCount", result.getWrongCount());
            json.put("emptyCount", result.getEmptyCount());
            json.put("totalScore", result.getTotalScore());
            json.put("speedBonus", result.getSpeedBonus());
            json.put("totalTimeUsed", result.getTotalTimeUsed());
            json.put("completedAt", result.getCompletedAt() != null ? result.getCompletedAt() : "");

            prefs.edit()
                    .putString(buildKey(uid, examType, testNumber), json.toString())
                    .putString(buildKey(uid, examType), json.toString())
                    .apply();
        } catch (Exception e) {
            timber.log.Timber.w(e, "Failed to save exam result to store");
        }
    }

    public void saveResult(String uid, ExamType examType, ScoreResult result) {
        saveResult(uid, examType, 1, result);
    }

    public ScoreResult getResult(String uid, ExamType examType, int testNumber) {
        if (uid == null || examType == null) return null;
        String raw = prefs.getString(buildKey(uid, examType, testNumber), null);
        if (raw == null && testNumber <= 1) {
            raw = prefs.getString(buildKey(uid, examType), null);
        }
        if (raw == null) return null;
        try {
            JSONObject json = new JSONObject(raw);
            ScoreResult result = new ScoreResult();
            result.setExamType(json.optString("examType", examType.name()));
            result.setExamTitle(json.optString("examTitle", examType.getTitle()));
            result.setNetScore(json.optInt("netScore", 0));
            result.setCorrectCount(json.optInt("correctCount", 0));
            result.setWrongCount(json.optInt("wrongCount", 0));
            result.setEmptyCount(json.optInt("emptyCount", 0));
            result.setTotalScore(json.optInt("totalScore", 0));
            result.setSpeedBonus(json.optInt("speedBonus", 0));
            result.setTotalTimeUsed(json.optInt("totalTimeUsed", 0));
            result.setCompletedAt(json.optString("completedAt", ""));
            return result;
        } catch (Exception e) {
            timber.log.Timber.w(e, "Failed to parse exam result from store");
            return null;
        }
    }

    public ScoreResult getResult(String uid, ExamType examType) {
        return getResult(uid, examType, 1);
    }

    public boolean hasResult(String uid, ExamType examType, int testNumber) {
        if (uid == null || examType == null) return false;
        return prefs.contains(buildKey(uid, examType, testNumber))
                || (testNumber <= 1 && prefs.contains(buildKey(uid, examType)));
    }

    public boolean hasResult(String uid, ExamType examType) {
        return hasResult(uid, examType, 1);
    }

    public Map<ExamType, ScoreResult> getAllResults(String uid) {
        Map<ExamType, ScoreResult> map = new EnumMap<>(ExamType.class);
        if (uid == null) return map;
        for (ExamType type : ExamType.values()) {
            ScoreResult res = getResult(uid, type);
            if (res != null) {
                map.put(type, res);
            }
        }
        return map;
    }

    private String buildKey(String uid, ExamType type, int testNumber) {
        return uid + "_" + type.name() + (testNumber > 0 ? ("_" + testNumber) : "");
    }

    private String buildKey(String uid, ExamType type) {
        return uid + "_" + type.name();
    }
}
