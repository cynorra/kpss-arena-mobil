package com.cynorra.kpssarena.domain.model;
public class LeaderboardEntry {
    private int rank;
    private String uid;
    private String username;
    private String photoUrl;
    private int totalScore;
    private int netScore;
    private int speedBonus;
    private int correctCount;
    private int wrongCount;
    private boolean isCurrentUser;
    private int previousRank;
    private int rankChange;
    public LeaderboardEntry() {}
    public LeaderboardEntry(int rank, String uid, String username, String photoUrl,
                            int totalScore, int netScore, int speedBonus) {
        this.rank = rank;
        this.uid = uid;
        this.username = username;
        this.photoUrl = photoUrl;

        this.totalScore = totalScore;
        this.netScore = netScore;
        this.speedBonus = speedBonus;
    }
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
    public int getNetScore() { return netScore; }
    public void setNetScore(int netScore) { this.netScore = netScore; }
    public int getSpeedBonus() { return speedBonus; }
    public void setSpeedBonus(int speedBonus) { this.speedBonus = speedBonus; }
    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }
    public int getWrongCount() { return wrongCount; }
    public void setWrongCount(int wrongCount) { this.wrongCount = wrongCount; }
    public boolean isCurrentUser() { return isCurrentUser; }
    public void setCurrentUser(boolean currentUser) { isCurrentUser = currentUser; }
    public int getPreviousRank() { return previousRank; }
    public void setPreviousRank(int previousRank) { this.previousRank = previousRank; }
    public int getRankChange() { return rankChange; }
    public void setRankChange(int rankChange) { this.rankChange = rankChange; }

    public String getRankBadge() {
        return String.valueOf(rank);
    }
}
