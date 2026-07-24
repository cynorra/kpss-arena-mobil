package com.cynorra.kpssarena.domain.model;
import java.util.List;
import java.util.Map;

public class StatsData {
    private int totalTests;
    private int totalQuestionsSolved;
    private int totalCorrect;
    private int totalWrong;
    private double overallAccuracy;
    private int currentStreak;
    private int longestStreak;
    private double averageScore;
    private int highestScore;
    private double averageTimePerTest;
    private Map<String, Double> weeklyNetAverages;
    private Map<String, Double> categoryAccuracy;
    private List<DailyScoreEntry> dailyScores;
    private int totalMatchesPlayed;
    private int totalMatchesWon;
    private double matchWinRate;
    public StatsData() {}
    public int getTotalTests() { return totalTests; }
    public void setTotalTests(int totalTests) { this.totalTests = totalTests; }
    public int getTotalQuestionsSolved() { return totalQuestionsSolved; }
    public void setTotalQuestionsSolved(int totalQuestionsSolved) { this.totalQuestionsSolved = totalQuestionsSolved; }
    public int getTotalCorrect() { return totalCorrect; }
    public void setTotalCorrect(int totalCorrect) { this.totalCorrect = totalCorrect; }
    public int getTotalWrong() { return totalWrong; }
    public void setTotalWrong(int totalWrong) { this.totalWrong = totalWrong; }
    public double getOverallAccuracy() { return overallAccuracy; }
    public void setOverallAccuracy(double overallAccuracy) { this.overallAccuracy = overallAccuracy; }
    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    public int getLongestStreak() { return longestStreak; }

    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }
    public double getAverageScore() { return averageScore; }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }
    public int getHighestScore() { return highestScore; }
    public void setHighestScore(int highestScore) { this.highestScore = highestScore; }
    public double getAverageTimePerTest() { return averageTimePerTest; }
    public void setAverageTimePerTest(double averageTimePerTest) { this.averageTimePerTest = averageTimePerTest; }
    public Map<String, Double> getWeeklyNetAverages() { return weeklyNetAverages; }
    public void setWeeklyNetAverages(Map<String, Double> weeklyNetAverages) { this.weeklyNetAverages = weeklyNetAverages; }
    public Map<String, Double> getCategoryAccuracy() { return categoryAccuracy; }
    public void setCategoryAccuracy(Map<String, Double> categoryAccuracy) { this.categoryAccuracy = categoryAccuracy; }
    public List<DailyScoreEntry> getDailyScores() { return dailyScores; }
    public void setDailyScores(List<DailyScoreEntry> dailyScores) { this.dailyScores = dailyScores; }
    public int getTotalMatchesPlayed() { return totalMatchesPlayed; }
    public void setTotalMatchesPlayed(int totalMatchesPlayed) { this.totalMatchesPlayed = totalMatchesPlayed; }
    public int getTotalMatchesWon() { return totalMatchesWon; }
    public void setTotalMatchesWon(int totalMatchesWon) { this.totalMatchesWon = totalMatchesWon; }
    public double getMatchWinRate() { return matchWinRate; }
    public void setMatchWinRate(double matchWinRate) { this.matchWinRate = matchWinRate; }
    public static class DailyScoreEntry {
        private String date;
        private int netScore;
        private int totalScore;
        public DailyScoreEntry(String date, int netScore, int totalScore) {
            this.date = date;
            this.netScore = netScore;
            this.totalScore = totalScore;
        }

        public String getDate() { return date; }
        public int getNetScore() { return netScore; }
        public int getTotalScore() { return totalScore; }
    }
}
