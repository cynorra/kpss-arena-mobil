package com.cynorra.kpssarena.presentation.admin;
public class ScheduledTestItem {
    private final String date;
    private final int questionCount;
    private final boolean active;
    public ScheduledTestItem(String date, int questionCount, boolean active) {
        this.date = date;
        this.questionCount = questionCount;
        this.active = active;
    }
    public String getDate() { return date; }
    public int getQuestionCount() { return questionCount; }
    public boolean isActive() { return active; }
}
