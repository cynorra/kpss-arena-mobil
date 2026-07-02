package com.cynorra.kpssarena.data.local.entity;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "user_cache")
public class UserCacheEntity {
    @PrimaryKey
    @ColumnInfo(name = "uid")
    private String uid;
    @ColumnInfo(name = "username")
    private String username;
    @ColumnInfo(name = "total_score")
    private int totalScore;
    @ColumnInfo(name = "streak")
    private int streak;

    @ColumnInfo(name = "is_premium")
    private boolean isPremium;
    @ColumnInfo(name = "last_test_date")
    private String lastTestDate;
    @ColumnInfo(name = "cached_at")
    private long cachedAt;
    public UserCacheEntity() {
        this.cachedAt = System.currentTimeMillis();
    }
    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }
    public boolean isPremium() { return isPremium; }
    public void setPremium(boolean premium) { isPremium = premium; }
    public String getLastTestDate() { return lastTestDate; }
    public void setLastTestDate(String lastTestDate) { this.lastTestDate = lastTestDate; }
    public long getCachedAt() { return cachedAt; }
    public void setCachedAt(long cachedAt) { this.cachedAt = cachedAt; }
}
