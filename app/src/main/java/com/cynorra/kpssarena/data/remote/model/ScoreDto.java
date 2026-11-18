package com.cynorra.kpssarena.data.remote.model;
import java.util.Map;
public class ScoreDto {
    private String uid;
    private int netScore;
    private int speedBonus;
    private int totalScore;
    private String completedAt;
    private long durationSeconds;
    private Map<String, String> answers;
    private String examType;
    private String examTitle;
    private int correctCount;
    private int wrongCount;
    private int emptyCount;
    public ScoreDto() {}
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public int getNetScore() { return netScore; }
    public void setNetScore(int netScore) { this.netScore = netScore; }
    public int getSpeedBonus() { return speedBonus; }
    public void setSpeedBonus(int speedBonus) { this.speedBonus = speedBonus; }
    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
    public long getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(long durationSeconds) { this.durationSeconds = durationSeconds; }
    public Map<String, String> getAnswers() { return answers; }
    public void setAnswers(Map<String, String> answers) { this.answers = answers; }
    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }
    public String getExamTitle() { return examTitle; }
    public void setExamTitle(String examTitle) { this.examTitle = examTitle; }
    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }
    public int getWrongCount() { return wrongCount; }
    public void setWrongCount(int wrongCount) { this.wrongCount = wrongCount; }
    public int getEmptyCount() { return emptyCount; }
    public void setEmptyCount(int emptyCount) { this.emptyCount = emptyCount; }
}
