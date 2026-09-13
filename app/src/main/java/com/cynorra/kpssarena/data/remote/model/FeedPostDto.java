package com.cynorra.kpssarena.data.remote.model;
import java.util.List;
public class FeedPostDto {
    private String uid;
    private String username;
    private String photoUrl;
    private String type;
    private int score;
    private int secondaryValue;
    private String opponentUsername;
    private long createdAt;
    private List<String> likedBy;
    public FeedPostDto() {}
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getSecondaryValue() { return secondaryValue; }
    public void setSecondaryValue(int secondaryValue) { this.secondaryValue = secondaryValue; }
    public String getOpponentUsername() { return opponentUsername; }
    public void setOpponentUsername(String opponentUsername) { this.opponentUsername = opponentUsername; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public List<String> getLikedBy() { return likedBy; }
    public void setLikedBy(List<String> likedBy) { this.likedBy = likedBy; }
}
