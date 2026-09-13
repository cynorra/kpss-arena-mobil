package com.cynorra.kpssarena.domain.model;
import java.util.List;
import java.util.Map;
public class ScoreResult implements java.io.Serializable {
    private int correctCount;
    private int wrongCount;
    private int emptyCount;

    private int netScore;
    private int speedBonus;
    private int totalScore;
    private int totalTimeUsed;
    private int averageTimePerQuestion;
    private List<Question> questions;
    private Map<String, String> answers;
    private String completedAt;
    private int rank;
    private int totalParticipants;
    public ScoreResult() {}
    public ScoreResult(int correctCount, int wrongCount, int emptyCount,
                       int netScore, int speedBonus, int totalScore,
                       int totalTimeUsed, List<Question> questions,
                       Map<String, String> answers) {
        this.correctCount = correctCount;
        this.wrongCount = wrongCount;
        this.emptyCount = emptyCount;
        this.netScore = netScore;
        this.speedBonus = speedBonus;
        this.totalScore = totalScore;
        this.totalTimeUsed = totalTimeUsed;
        this.questions = questions;
        this.answers = answers;
        this.averageTimePerQuestion = totalTimeUsed > 0 && questions != null && !questions.isEmpty()
                ? totalTimeUsed / questions.size() : 0;
    }
    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }
    public int getWrongCount() { return wrongCount; }
    public void setWrongCount(int wrongCount) { this.wrongCount = wrongCount; }
    public int getEmptyCount() { return emptyCount; }
    public void setEmptyCount(int emptyCount) { this.emptyCount = emptyCount; }
    public int getNetScore() { return netScore; }

    public void setNetScore(int netScore) { this.netScore = netScore; }
    public int getSpeedBonus() { return speedBonus; }
    public void setSpeedBonus(int speedBonus) { this.speedBonus = speedBonus; }
    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
    public int getTotalTimeUsed() { return totalTimeUsed; }
    public void setTotalTimeUsed(int totalTimeUsed) { this.totalTimeUsed = totalTimeUsed; }
    public int getAverageTimePerQuestion() { return averageTimePerQuestion; }
    public void setAverageTimePerQuestion(int averageTimePerQuestion) {
        this.averageTimePerQuestion = averageTimePerQuestion;
    }
    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
    public Map<String, String> getAnswers() { return answers; }
    public void setAnswers(Map<String, String> answers) { this.answers = answers; }
    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
    public int getTotalParticipants() { return totalParticipants; }
    public void setTotalParticipants(int totalParticipants) { this.totalParticipants = totalParticipants; }
    public double getAccuracy() {
        int totalAnswered = correctCount + wrongCount;
        return totalAnswered > 0 ? (double) correctCount / totalAnswered * 100 : 0;
    }
}
