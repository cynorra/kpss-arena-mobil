package com.cynorra.kpssarena.domain.model;

import java.io.Serializable;

public class BranchExam implements Serializable {

    private final String id;
    private final ExamType examType;
    private final int testNumber;
    private final String title;
    private final String subtitle;
    private final int totalQuestions;
    private final int durationMinutes;
    private ScoreResult lastResult;

    public BranchExam(String id, ExamType examType, int testNumber, String title,
                      String subtitle, int totalQuestions, int durationMinutes) {
        this.id = id;
        this.examType = examType;
        this.testNumber = testNumber;
        this.title = title;
        this.subtitle = subtitle;
        this.totalQuestions = totalQuestions;
        this.durationMinutes = durationMinutes;
    }

    public String getId() {
        return id;
    }

    public ExamType getExamType() {
        return examType;
    }

    public int getTestNumber() {
        return testNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public ScoreResult getLastResult() {
        return lastResult;
    }

    public void setLastResult(ScoreResult lastResult) {
        this.lastResult = lastResult;
    }

    public boolean isCompleted() {
        return lastResult != null;
    }
}
