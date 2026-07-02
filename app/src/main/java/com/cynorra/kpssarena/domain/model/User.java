package com.cynorra.kpssarena.domain.model;
import java.util.List;
public class User {
    private String uid;
    private String username;
    private String email;
    private String photoUrl;
    private int totalScore;
    private int weeklyScore;
    private int monthlyScore;
    private int streak;
    private String lastTestDate;
    private List<String> following;

    private List<String> followers;
    private List<Badge> badges;
    private boolean isPremium;
    private long createdAt;
    private int rank;
    // Constructor
    public User() {
        this.following = java.util.Collections.emptyList();
        this.followers = java.util.Collections.emptyList();
        this.badges = java.util.Collections.emptyList();
    }
    public User(String uid, String username, String email, String photoUrl,
                int totalScore, int weeklyScore, int monthlyScore,
                int streak, String lastTestDate, List<String> following,
                List<String> followers, List<Badge> badges, boolean isPremium,
                long createdAt) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.photoUrl = photoUrl;
        this.totalScore = totalScore;
        this.weeklyScore = weeklyScore;
        this.monthlyScore = monthlyScore;
        this.streak = streak;
        this.lastTestDate = lastTestDate;
        this.following = following != null ? following : java.util.Collections.emptyList();
        this.followers = followers != null ? followers : java.util.Collections.emptyList();
        this.badges = badges != null ? badges : java.util.Collections.emptyList();
        this.isPremium = isPremium;
        this.createdAt = createdAt;
    }
    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
    public int getWeeklyScore() { return weeklyScore; }
    public void setWeeklyScore(int weeklyScore) { this.weeklyScore = weeklyScore; }
    public int getMonthlyScore() { return monthlyScore; }
    public void setMonthlyScore(int monthlyScore) { this.monthlyScore = monthlyScore; }
    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }
    public String getLastTestDate() { return lastTestDate; }
    public void setLastTestDate(String lastTestDate) { this.lastTestDate = lastTestDate; }
    public List<String> getFollowing() { return following; }
    public void setFollowing(List<String> following) { this.following = following; }
    public List<String> getFollowers() { return followers; }
    public void setFollowers(List<String> followers) { this.followers = followers; }
    public List<Badge> getBadges() { return badges; }
    public void setBadges(List<Badge> badges) { this.badges = badges; }
    public boolean isPremium() { return isPremium; }
    public void setPremium(boolean premium) { isPremium = premium; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
}
