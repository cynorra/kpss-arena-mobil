package com.cynorra.kpssarena.data.remote.model;
import java.util.List;
public class UserDto {
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
    private List<String> badges;
    private boolean premium;
    private long createdAt;
    public UserDto() {}
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
    public List<String> getBadges() { return badges; }
    public void setBadges(List<String> badges) { this.badges = badges; }
    public boolean isPremium() { return premium; }
    public void setPremium(boolean premium) { this.premium = premium; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
