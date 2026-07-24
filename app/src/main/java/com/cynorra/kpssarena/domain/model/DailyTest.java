package com.cynorra.kpssarena.domain.model;
import java.util.List;
public class DailyTest {
    private String date;
    private List<Question> questions;
    private long publishedAt;
    private boolean isActive;
    private boolean isCompleted;
    private int totalQuestions;
    private int timePerQuestion;
    public DailyTest() {
        this.timePerQuestion = 45; // 45 seconds default
        this.totalQuestions = 12;
    }
    public DailyTest(String date, List<Question> questions, long publishedAt, boolean isActive) {
        this.date = date;
        this.questions = questions;
        this.publishedAt = publishedAt;
        this.isActive = isActive;
        this.timePerQuestion = 45;
        this.totalQuestions = questions != null ? questions.size() : 12;
    }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
    public long getPublishedAt() { return publishedAt; }
    public void setPublishedAt(long publishedAt) { this.publishedAt = publishedAt; }
    public boolean isActive() { return isActive; }

    public void setActive(boolean active) { isActive = active; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public int getTimePerQuestion() { return timePerQuestion; }
    public void setTimePerQuestion(int timePerQuestion) { this.timePerQuestion = timePerQuestion; }
    public int getTotalTime() {
        return totalQuestions * timePerQuestion;
    }
    public int getAnsweredCount() {
        int count = 0;
        if (questions != null) {
            for (Question q : questions) {
                if (q.isAnswered()) count++;
            }
        }
        return count;
    }
}
